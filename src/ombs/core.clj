(ns ombs.core
  "contains main logic"
  (:require [ombs.db :as db] 
            [noir.validation :as valids]
            ))

(defn reg-ok? [username pass1 pass2] 
  "check user post params on errors, and return false if some is not pass"
  (if (and (not (empty? username)) (> (count pass1) 8) (= pass1 pass2) )
    true 
    false
    )
  )

(defn rate [student?]
  "Return rate for user. student - 0.5; else - 1."
  (if (= "on" student?)
    0.5
    1.0 ) )
