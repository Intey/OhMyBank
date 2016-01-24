(ns ombs.event.validator
  (:require
    [schema.core :as s]
    [compojure.api.sweet :refer [describe]]
    [ring.util.http-response :as resp]
    ))

(s/defschema Event
  {:id s/Int
   :author s/Str
   :name s/Str
   :date s/Str
   :price s/Num
   :rest s/Num
   :parts s/Num
   :status s/Str })


(defrecord Validator [f error])
(defn errorId [msg] {:errors {:id msg}})
(defn with-validation
  "Execute validator on args, and if it's return nil"
  ([validator msg f & args]
   (let [vld (Validator. validator (errorId msg))]
   (if (apply (:f vld) args)
     (resp/ok (apply f args))
     (resp/bad-request (:error vld))))))
