(ns mrkuc.parser)

(defn tokenize [s]
  (->> (re-seq #"[\s,]*(~@|[\[\]{}()'`~^@]|\"(?:\\.|[^\\\"])*\"|;.*|[^\s\[\]{}('\"`,;)]*)" s)
       (map first)
       (map clojure.string/trim)))

;;(tokenize "(defn [a] (str a \"hoge\"))")

;; from https://gist.github.com/kachayev/b5887f66e2985a21a466

(defn any [input]
  (if-let [c (first input)]
    (list [c (.substring input 1)])
    '()))

(defn failure [_]
  '())

(defn parse [parser input]
  (parser input))

(defn parse-all [parser input]
  (->> input
       (parse parser)
       (filter #(= "" (second %)))
       (ffirst)))

(defn return [v]
  (fn [input] (list [v input])))

(defn >>= [m f]
  (fn [input]
    (->> input
         (parse m)
         (mapcat (fn [[v tail]] (parse (f v) tail))))))

(defn merge-bind [body bind]
  (if (and (not= clojure.lang.Symbol (type bind))
           (= 3 (count bind))
           (= '<- (second bind)))
    `(>>= ~(last bind) (fn [~(first bind)] ~body))
    `(>>= ~bind (fn [~'_] ~body))))

(defmacro do* [& forms]
  (reduce merge-bind (last forms) (reverse (butlast forms))))

(defn- -sat [pred]
  (>>= any (fn [v] (if (pred v) (return v) failure))))

(defn- -char-cmp [f]
  (fn [c] (-sat (partial f (first c)))))

(def match (-char-cmp =))

(def none-of (-char-cmp not=))

(defn- -from-re [re]
  (-sat (fn [v] (not (nil? (re-find re (str v)))))))

(def digit (-from-re #"[0-9]"))

(def letter (-from-re #"[a-zA-Z]"))

;; (parse (match "c") "clojure1.9")
;; (parse letter "clojure1.9")
;; (parse digit "1.9clojure")

;; (ab)
(defn and-then [p1 p2]
  (do*
   (r1 <- p1)
   (r2 <- p2)
   (return (str r1 r2))))

;; (a|b)
(defn or-else [p1 p2]
  (fn [input]
    (lazy-cat (parse p1 input) (parse p2 input))))

(declare plus)
(declare optional)

;; (a*)
(defn many [parser]
  (optional (plus parser)))

;; (a+) equals to (aa*)
(defn plus [parser]
  (do*
   (a <- parser)
   (as <- (many parser))
   (return (cons a as))))

;; (a?)
(defn optional [parser]
  (or-else parser (return "")))

(def space (or-else (match " ") (match "\n")))
(def spaces (many space))

(defn string [s]
  (reduce and-then (map #(match (str %)) s)))

;; (def clojure-version (do*
;;                       (string "clojure")
;;                       (match " ")
;;                       (major <- digit)
;;                       (match ".")
;;                       (minor <- digit)
;;                       (return (str "major: " major "; minor: " minor))))

;; (parse-all clojure-version "clojure 1.7")

