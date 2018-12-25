(ns mrkuc.core)

(defn- READ []
  (read-line))

(defn- EVAL [s]
  s)

(defn- PRINT [s]
  (println s))

(defn rep []
  (->
   (READ)
   (EVAL)
   (PRINT)))


