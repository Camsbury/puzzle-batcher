(ns puzzle-batcher.plot)

(def tap-plot
  {:width 600
   :height 400
   :axis {:grid false}
   :config {:legend
            {:labelColor "#94a3b8"
             :titleColor "#94a3b8"}}
   :background nil})

;; #1f2937 - bg color for plots in the tap inspector
;; #374151 - label color

(def tap-axes
  {:titleColor "#94a3b8"
   :tickColor "#374151"
   :labelColor "#94a3b8"
   :grid false})

(defn pdf
  [data & {:keys [title bandwidth]
           :or   {bandwidth (/ (- (apply max data) (apply min data)) 100)}}]
  {:data
   [{:name   "input"
     :values (map (fn [v] {:val v}) data)}
    {:name      "density"
     :source    "input"
     :transform [{:type   "density"
                  :method "pdf"
                  :distribution
                  {:function  "kde"
                   :field     :val
                   :bandwidth bandwidth}}]}]
   :title      {:text  title
                :color "white"}
   :height     400
   :width      600
   :background nil
   :scales     [{:name   "xscale"
                 :type   "linear"
                 :range  "width"
                 :domain {:data  "input"
                          :field "val"}}
                {:name   "yscale"
                 :type   "linear"
                 :range  "height"
                 :domain {:data  "density"
                          :field "density"}}]
   :marks      [{:type   "area",
                 :from   {:data "density"}
                 :encode {:update
                          {:x    {:scale "xscale"
                                  :field "value"}
                           :y    {:scale "yscale"
                                  :field "density"}
                           :y2   {:scale "yscale"
                                  :value 0}
                           :fill {:value "#bef264"}}}}]
   :axes       [{:scale       "xscale"
                 :orient      "bottom"
                 :field       "value"
                 :domainColor "white"
                 :tickColor   "white"
                 :titleColor  "white"
                 :labelColor  "white"}
                {:scale       "yscale"
                 :orient      "left"
                 :field       "density"
                 :domainColor "white"
                 :tickColor   "white"
                 :titleColor  "white"
                 :labelColor  "white"}]
   :config
   {:style
    {:cell
     {:stroke "transparent"}}}})

(defn histogram
  [data & {:keys [title max-bins]
           :or   {max-bins 100}}]
  {:data {:values (map (fn [v] {:val v}) data)}
   :title {:text title
           :color "white"}
   :height 400
   :width 600
   :background nil
   :mark {:type "bar"
          :color "#bef264"}
   :encoding {:x {:field :val
                  :bin {:maxbins max-bins}
                  :axis {:domainColor "white"
                         :tickColor   "white"
                         :titleColor  "white"
                         :labelColor  "white"}}
              :y {:aggregate "count"
                  :axis {:domainColor "white"
                         :tickColor   "white"
                         :titleColor  "white"
                         :labelColor  "white"}}}
   :config
   {:style
    {:cell
     {:stroke "transparent"}}}})



(defn histogram2
  [data & {:keys [title max-bins]
           :or   {max-bins 30}}]
  {:data       {:values
                (let [max-datum  (apply max data)
                      min-datum  (apply min data)
                      data-range (- max-datum min-datum)
                      bin-width  (max 1 (quot data-range max-bins))]
                  (loop [data      data
                         bin-limit bin-width
                         counts    []]
                    (if (seq data)
                      (let [[under over] (split-with #(< % bin-limit) data)]
                        (recur
                         over
                         (+ bin-limit bin-width)
                         (conj counts {:bin-limit bin-limit
                                       :count     (count under)})))
                      counts)))}
   :title      {:text  title
                :color "white"}
   :height     400
   :width      600
   :background nil
   :mark       {:type  "bar"
                :size  10
                :color "#bef264"}
   :encoding   {:x {:field :bin-limit
                    :type "quantitative"
                    :axis  {:domainColor "white"
                            :tickColor   "white"
                            :titleColor  "white"
                            :labelColor  "white"}}
                :y {:field :count
                    :type "quantitative"
                    :axis  {:domainColor "white"
                            :tickColor   "white"
                            :titleColor  "white"
                            :labelColor  "white"}}}
   :config
   {:style
    {:cell
     {:stroke "transparent"}}}})

(defn seq->plot
  [data & {:keys [mark-type
                  highlights
                  title
                  y-zero-scale?]
           :or   {mark-type     "line"
                  highlights    nil
                  y-zero-scale? true}}]
  {:data
   {:values
    (map
     (fn [x y]
       {:x x
        :y y
        :highlight (contains? (set highlights) x)})
     (range)
     data)}
   :title {:text title
           :color "white"}
   :height 400
   :width 600
   :background nil
   :encoding
   {:x
    {:field :x
     :type "quantitative"
     :axis {:domainColor "white"
            :tickColor   "white"
            :titleColor  "white"
            :labelColor  "white"}}
    :y
    {:field :y
     :type "quantitative"
     :scale {:zero y-zero-scale?}
     :axis {:domainColor "white"
            :tickColor   "white"
            :titleColor  "white"
            :labelColor  "white"}}}
   ;; :mark {:type  mark-type
   ;;        :color "#bef264"}
   :layer
     [{:mark {:type  mark-type
              :color "#bef264"}}
      {:mark {:type "rule"
              :color "red"}
       :transform [{:filter "datum.highlight"}]
       :encoding {:x {:field :x
                      :type "quantitative"}}}]
   :config
   {:style
    {:cell
     {:stroke "transparent"}}}})

(defn seqs->plot
  ;; up to 2 (because of colors)
  [datasets &
   {:keys [mark-type y-zero-scale?]
    :or   {mark-type     "line"
           y-zero-scale? true}}]
  (let [colors ["#bef264"
                "#78DDEC"]]
    {:layer
     (mapv
      (fn [[title data] color]
        {:data {:values (map (fn [x y] {:x x :y y}) (range) data)}
         :encoding
         {:x
          {:field :x
           :type  "quantitative"}
          :y
          {:field :y
           :type  "quantitative"
           :scale {:zero y-zero-scale?}}}
         :mark {:type  mark-type
                :color color}})
      datasets
      colors)
     :encoding
     {:x {:axis {:domainColor "white"
                 :tickColor   "white"
                 :titleColor  "white"
                 :labelColor  "white"}}
      :y {:axis {:domainColor "white"
                 :tickColor   "white"
                 :titleColor  "white"
                 :labelColor  "white"}}}
     :height     400
     :width      600
     :background nil
     :config
     {:style
      {:cell
       {:stroke "transparent"}}}}))

(defn ts->plot
  [data & {:keys [date-name
                  val-name
                  title
                  mark-type
                  y-zero-scale?]
           :or   {mark-type     "line"
                  y-zero-scale? true}}]
  {:data       {:values data}
   :title      {:text  title
                :color "white"}
   :height     400
   :width      600
   :background nil
   :encoding
   {:x
    {:field date-name
     :type  "temporal"
     :axis  {:domainColor "white"
             :tickColor   "white"
             :titleColor  "white"
             :labelColor  "white"}}
    :y
    {:field val-name
     :type  "quantitative"
     :scale {:zero y-zero-scale?}
     :axis  {:domainColor "white"
             :tickColor   "white"
             :titleColor  "white"
             :labelColor  "white"}}}
   :mark       {:type  mark-type
                :color "#bef264"}
   :config
   {:style
    {:cell
     {:stroke "transparent"}}}})
