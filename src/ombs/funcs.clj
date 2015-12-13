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
    (if-let [r (re-find  #"\d+" s)]
      (Integer. r)
      0)))

(defn nil-fix
 "Replace nil value to 0"
 [v] (if (nil? v) 0 v))

(defn valuer [sq ks]
  "Extract keys ks from hashes in sequence sq."
  (map #(replace % ks) sq))

(defn part-price [event-price parts] (/ event-price parts))

(defn with-log [f]
  (print f)
  (f))
