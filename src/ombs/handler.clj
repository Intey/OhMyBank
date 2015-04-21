(ns ombs.handler
  (:require
    [ombs.view :as view]
    [ombs.db :as db]
    [noir.response :refer [redirect] ]))

(defn index [& error]
  "Handler. show index page"
  (view/index {:error error}))

(defn user [params]
  "Generate user page, with his name and events."
  (view/user (:username params))
  )
