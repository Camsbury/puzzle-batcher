(ns puzzle-batcher.scratch
  (:require
   [clojure.string :as str]
   [puzzle-batcher.core :as core]
   [puzzle-batcher.plot :as plot]
   [portal.api :as p]))

(defn grab-ids
  [base-rating]
  (->> @core/puzzles
       (filter #(> (:nb-plays %) 500))
       (filter #(<= (count (:moves %)) 4))
       (filter #(> (:rating %) base-rating))
       (filter #(< (:rating %) (+ 10 base-rating)))
       shuffle
       (map :puzzle-id)
       (str/join "\n")))

(comment
  (grab-ids 900)


  (def p (p/open))
  (add-tap #'p/submit)
  (->> @core/puzzles
       (filter #(<= (count (:moves %)) 4))
       (filter #(>= (:nb-plays %) 500))
       (filter #(>= (:rating %) 1800))
       (filter #(<= (:rating %) 2300))
       (map :rating)
       (#(plot/histogram % :title "rating"))
       (#(with-meta
           %
           {:portal.viewer/default :portal.viewer/vega-lite}))
       tap>)

  (def play-counts
    (->> @core/puzzles
         (filter #(<= (count (:moves %)) 4))
         (map :nb-plays)
         sort))

  (defn quantile
    [q xs]
    (let [n (count xs)
          i (* q n)]
      (nth xs i)))

  (quantile 0.6 play-counts)




  [:attraction
   :capturing-defender
   :clearance
   :defensive-move
   :deflection
   :discovered-attack
   :double-check
   :fork
   :hanging-piece
   :interference
   :intermezzo
   :pin
   :quiet-move
   :sacrifice
   :skewer
   :trapped-piece
   :x-ray-attack
   :zugzwang]

  [:opening
   :middlegame
   :endgame
   :kingside-attack
   :queenside-attack
   :attacking-f-2-f-7
   :pawn-endgame
   :rook-endgame
   ]



  )



