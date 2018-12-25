(ns mrkuc.clisp
  (:gen-class)
  (:require [mrkuc.core :as core]))

(defn -main
  [& args]
  (loop []
    (println "user>");; printにすると何故かcore/repのあとに表示されてしまう。
    (core/rep)
    (recur)))
