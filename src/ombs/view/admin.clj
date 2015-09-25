(ns ombs.view.admin
  (:require
    [net.cgrand.enlive-html :as h]
    [noir.session :as sess]
    [ombs.validate :refer [errors-string]]
    [ombs.core :as core]
    [ombs.db.admin :refer [get-fees]]
    [ombs.view.event :refer [event-elem]]
    [ombs.view.fees :refer [fee-elem]]
    ))

(h/deftemplate page "../resources/public/user.html" [username]
  [:#user] (h/content username)
  [:#error] (h/content (errors-string))
  [:.debt] (h/content (str (core/debt username)))
  [:section.events :> :article] (h/content (map #(event-elem %) (core/events)))
  [:section.fees :> :article] (h/content (map #(fee-elem %)) (get-fees))
  )
