(ns ombs.view.fees
  (:require
    [net.cgrand.enlive-html :as h]
    [net.cgrand.reload :as reload]
    ))

(h/defsnippet fee-elem "../resources/public/fee.html" [:.fee] [{:keys [id user event edate date money parts]}]
  [:.fee] (h/set-attr :id id)
  [:.user] (h/content user)
  [:.event-name] (h/content (str event " " edate))
  [:.event-date] (h/content date)
  [:.money] (h/content (str money))
  [:.parts] (h/content (str parts))
  )

(reload/auto-reload *ns*)
