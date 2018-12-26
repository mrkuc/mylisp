(ns mrkuc.parser)

(defn tokenize [s]
  (map clojure.string/trim (map first (re-seq #"[\s,]*(~@|[\[\]{}()'`~^@]|\"(?:\\.|[^\\\"])*\"|;.*|[^\s\[\]{}('\"`,;)]*)" s))))

;;(tokenize "(defn [a] (str a \"hoge\"))")


;; TODO implement monadic-parser
