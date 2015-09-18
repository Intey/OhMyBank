(ns ombs.funcs "contains generic functions")

(defn in?
  "checks if value contained in collection. Used for vectors. For maps, use 'some'"
  [v coll]
  (boolean (some #(= v %) coll))
  )

(defn as-vec
  "get a vector or value and represents it as vector. [1 2 3] -> [1 2 3]; 1 -> [1]"
  [x] (if-not (vector? x)
        (conj [] x)
        x))

(defn parse-int [s]
  (if (nil? s)
    0
    (when-let [r (re-find  #"\d+" s )] (Integer. r))))

(defn nil-fix
 "Replace nil value to 0"
 [v] (if (nil? v) 0 v))
