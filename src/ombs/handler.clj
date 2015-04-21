(ns ombs.handler
  (:require
    [ombs.view :as view]
    [ombs.db :as db]
    [noir.response :refer [redirect] ]))

(defn index [& error]
  "Handler. show index page"
  (view/index {:error error}))

(defn user [ctxt]
  "Generate user page, with his name and events."
  (view/user ctxt)
  )

(defn login [ctxt]
  "Redirect user to page with his name"
  (print (str "handle logining with params: '" ctxt "'."))
  (redirect (str "/user/" ctxt))
  )
