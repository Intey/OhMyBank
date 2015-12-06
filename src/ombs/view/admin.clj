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

(h/deftemplate page "../resources/public/admin.html"
  [username & [page page-size]] ;
  [:#user] (h/content username)
  [:#error] (h/content (errors-string))
  [:.debt] (h/content (str (core/debt username)))
  [:section.events] (h/content (map #(event-elem %) (core/events)))
  [:section.fees] (let [fees (get-fees)]
                    (if (not= fees {})
                      (h/content (map #(fee-elem %) fees)) ; partition by 100, to get pages. then map on pages.
                      nil
                      ))
  ;[:.page-line] (h/content (get-pages)
  )

(reload/auto-reload *ns*)
