(ns puzzle-batcher.core
  (:require
   ["chessground"  :refer [Chessground]]
   ["chess.js"     :refer [Chess]]
   [uix.core.alpha :as uix]
   [uix.dom.alpha  :as uix.dom]))

(defn cg []
  (let [opts (clj->js {:fen (.fen (Chess.))})
        ref (uix/ref)
        cg-api (uix/state nil)]
    (uix/with-effect [ref]
      (when (and (not @cg-api) @ref)
        (reset! cg-api (Chessground. @ref opts)))
      #(when @cg-api (.destroy @cg-api)))

    ;; set config once api is set
    (uix/with-effect [@cg-api opts]
      (when @cg-api
        (.set @cg-api opts)
        ;; set shapes cannot be combined with :fen, so we call it after
        ;; https://github.com/lichess-org/chessground/issues/171
        #_#_
        (when shapes
          (.setShapes @cg-api (clj->js shapes)))
        (when autoShapes
          (.setAutoShapes @cg-api (clj->js autoShapes)))))

    [:div {:class ["w-full" "h-full"] :ref ref}]))

;; [chessground
;;        {:fen              fen
;;         :lastMove         #js [(get move "from") (get move "to")]
;;         :blockTouchScroll true
;;         ;; :viewOnly    true
;;         :coordinates      false
;;         :orientation      (if (#{"russmatney"} white-player)
;;                             "white" "black")
;;         :width            "220px"
;;         :height           "220px"
;;         :autoShapes       (->> [(when best
;;                                   (let [[orig dest] (->> best (partition 2 2)
;;                                                          (map #(apply str %)))]
;;                                     {:orig orig :dest dest :brush "blue"}))]
;;                                (remove nil?))}]

(uix.dom/render [cg] js/root)

