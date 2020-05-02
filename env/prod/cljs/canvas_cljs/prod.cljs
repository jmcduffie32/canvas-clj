(ns canvas-cljs.prod
  (:require [canvas-cljs.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
