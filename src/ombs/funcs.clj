(ns ombs.funcs "contains generic functions")

(defn in?
  "checks if value contained in collection. Used for vectors. For maps, use 'some'"
  [v coll]
  (boolean (some #(= v %) coll))
  )
