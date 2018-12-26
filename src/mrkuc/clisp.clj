(ns mrkuc.clisp
  (:gen-class)
  (:require [mrkuc.core :as core]))

(defn -main
  [& args]
  (loop []
    (print "user> ")
    (flush)
    (core/rep)
    (recur)))
