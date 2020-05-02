(ns canvas-cljs.canvas
  (:require
   [reagent.core :as r]))

(defonce state (r/atom
                {:snake {:body (list [1 1])
                         :dir [0 1]}
                 :apples (list [10 10])}))

(defonce interval (r/atom nil))

(defn clear-canvas [canvas]
  (let [ctx (.getContext canvas "2d")
        w (.-width canvas)
        h (.-height canvas)]
    (set! (.-fillStyle ctx) "white")
    (doto ctx
      (.beginPath)
      (.rect 0 0 w h)
      (.fill))))

(defn handle-keypress [event]
  (let [code (.-code event)
        new-dir (case code
                  "ArrowUp" [0 -1]
                  "ArrowDown" [0 1]
                  "ArrowLeft" [-1 0]
                  "ArrowRight" [1 0])]
    (println code new-dir)
    (swap! state assoc-in [:snake :dir] new-dir)))

(defn move-snake []
  (let [snake (:snake @state)
        body (:body snake)
        dir (:dir snake)]
    (swap! state
           assoc-in [:snake :body]
           (for [segment body]
             [(+ (segment 0) (dir 0)) (+ (segment 1) (dir 1))]))))

(defn draw-snake [ctx]
  (let [body (:body (:snake @state))]
    (doseq [[x y] body]
      (set! (.-fillStyle ctx) "black")
      (doto ctx
        (.beginPath)
        (.rect x y 10 10)
        (.fill)))))

(defn tick [canvas ctx]
  (let [snake (:snake @state)]
    (move-snake)
    (clear-canvas canvas)
    (draw-snake ctx)
    ))

(defn canvas-page []
  (r/create-class
   {:display-name "canvas-page"
    :component-did-mount
    (fn [this]
      (let [canvas (r/dom-node this)
            ctx (.getContext canvas "2d")]
        (.removeEventListener js/document "keydown" handle-keypress)
        (.addEventListener js/document "keydown" handle-keypress)
        (if (not (nil? @interval))
          (js/clearInterval @interval))
        (reset! interval (js/setInterval #(tick canvas ctx) (/ 1000 60)))))
    :reagent-render
    (fn []
      [:canvas#drawing1])
    }))
