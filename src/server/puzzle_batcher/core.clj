(ns puzzle-batcher.core
  (:require
   [camel-snake-kebab.core :as csk]
   [clojure.java.io  :as io]
   [clojure.data.csv :as csv]
   [clojure.string   :as str]
   [taoensso.nippy :as nippy]
   )
  (:import
   [java.io BufferedReader InputStreamReader]
   [com.github.luben.zstd ZstdInputStream]))

(def download-link "https://database.lichess.org/lichess_db_puzzle.csv.zst")

(defn clean-split
  [items]
  (into [] (remove #(= "" %) (str/split items #" "))))

(defn x-kebab
  [tags]
  (->> tags
       clean-split
       (map csk/->kebab-case-keyword)
       (into #{})))

#_
(def puzzles
  (delay
    (let [raw
          (with-open [compressed-input-stream (io/input-stream download-link)]
            (let [reader (-> compressed-input-stream
                             (ZstdInputStream.)
                             (InputStreamReader. "UTF-8")
                             (BufferedReader.))
                  sw  (java.io.StringWriter.)]
              (io/copy reader sw)
              (csv/read-csv (.toString sw))))
          headers (mapcat x-kebab (first raw))]
      (println "Downloaded " (count (rest raw)) " puzzles.")
      (->> raw
           rest
           (map zipmap (repeat headers))
           (map #(update % :opening-tags x-kebab))
           (map #(update % :moves clean-split))
           (map #(update % :themes x-kebab))
           (map #(update % :popularity parse-long))
           (map #(update % :rating parse-long))
           (map #(update % :rating-deviation parse-long))
           (map #(update % :nb-plays parse-long))))))

(def puzzles
  (delay
    (nippy/thaw-from-file "resources/puzzles.nippy")))

(comment
  (nippy/freeze-to-file
    "resources/puzzles.nippy"
    @puzzles))

#_
(def themes
  (->> @puzzles
       (reduce (fn [s p] (into s (:themes p))) #{})
       (into #{})
       sort))


(def themes
  [:advanced-pawn
   :advantage
   :anastasia-mate
   :arabian-mate
   :attacking-f-2-f-7
   :attraction
   :back-rank-mate
   :bishop-endgame
   :boden-mate
   :capturing-defender
   :castling
   :clearance
   :crushing
   :defensive-move
   :deflection
   :discovered-attack
   :double-bishop-mate
   :double-check
   :dovetail-mate
   :en-passant
   :endgame
   :equality
   :exposed-king
   :fork
   :hanging-piece
   :hook-mate
   :interference
   :intermezzo
   :kill-box-mate
   :kingside-attack
   :knight-endgame
   :long
   :master
   :master-vs-master
   :mate
   :mate-in-1
   :mate-in-2
   :mate-in-3
   :mate-in-4
   :mate-in-5
   :middlegame
   :one-move
   :opening
   :pawn-endgame
   :pin
   :promotion
   :queen-endgame
   :queen-rook-endgame
   :queenside-attack
   :quiet-move
   :rook-endgame
   :sacrifice
   :short
   :skewer
   :smothered-mate
   :super-gm
   :trapped-piece
   :under-promotion
   :very-long
   :vukovic-mate
   :x-ray-attack
   :zugzwang])
