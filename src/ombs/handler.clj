(ns ombs.handler
  (:require
    [ombs.view :as view]
    [ombs.db :as db]
    [noir.response :refer [redirect] ]))

(defn index [ctxt]
  "Handler. show index page"
  (view/index ctxt))

(defn user [params]
  "Generate user page, with his name and events."
  (view/user (:username params))
  )
