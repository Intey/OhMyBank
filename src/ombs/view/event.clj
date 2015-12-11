(ns ombs.view.event
  (:require
    [net.cgrand.enlive-html :as h]
    [net.cgrand.reload :as reload]
    [noir.session :as sess]
    [ombs.funcs :as f]
    [ombs.core :as core]
    [ombs.db.payment :as dbp]
    [ombs.validate :refer [errors-string]]
    [ombs.view.actions :as acts]
    ))

(h/defsnippet parts-snip "../resources/public/event.html" [:.parts] [parts match]
  ;   (actions/parts-snip rest parts) ; rest - actual; parts - is max
  [:.parts] (comp (h/set-attr :max (str parts)) (h/set-attr :value (str parts))) )

(defn fill-parts [parts match]
  (if (= 0 (f/nil-fix parts))
    nil
    (parts-snip parts match)))

(h/defsnippet event-elem "../resources/public/event.html" [:.event]
  [{:keys [id name price date author status parts rest] :as event}]

  [:.event]  (h/set-attr :id id)
  [:.name]   (h/content (str author "'s " name))
  [:.date]   (h/content (str date))
  [:.action] (partial acts/action-snip (acts/get-action event))
  [:.parts] (partial fill-parts rest)
  [:.participants] (h/content (str (dbp/get-participants id))) ;TODO: remove str
  )


(reload/auto-reload *ns*)
