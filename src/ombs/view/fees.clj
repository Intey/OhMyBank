(ns ombs.view.fees
  (:require 
    [net.cgrand.enlive-html :as h]
    ))

(h/defsnippet fee-elem "../resources/public/fee.html" [:.fee] [{:keys [user event edate date money]}]
  [:.user] (h/content user)
  [:.event] (h/content event)
  [:.edate] (h/content edate)
  [:.date] (h/content date)
  [:.money] (h/content money))
