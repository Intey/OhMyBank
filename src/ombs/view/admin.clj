(ns ombs.view.admin
  (:require
    [net.cgrand.enlive-html :as h]
    [net.cgrand.reload :as reload]
    [noir.session :as sess]
    [ombs.validate :refer [errors-string]]
    [ombs.core :as core]
    [ombs.db.admin :refer [get-fees]]
    [ombs.view.event :refer [event-elem]]
    [ombs.view.fees :refer [fee-elem]]
    ))

(h/deftemplate page "public/admin.html" [username]
  [:#user] (h/content username)
  [:#error] (h/content (errors-string))
  [:.debt] (h/content (str (core/debt username)))
  [:section.events :> :article] (h/content (map #(event-elem %) (core/events)))
  [:section :> :table :> :tbody] (h/content (map #(fee-elem %) (get-fees)))
  )

(reload/auto-reload *ns*)
