(ns ombs.view.event
  (:require
    [net.cgrand.enlive-html :as h]
    [net.cgrand.reload :as reload]
    [noir.session :as sess]
    [noir.validation :as vld]
    [ombs.core :as core]
    [ombs.validate :refer [errors-string]]
    [ombs.view.dom :refer [set-attr-class rm-attr-class content-wrap]]
    [ombs.view.actions :as actions]
    ))

(h/defsnippet action "../resources/public/action.html" [:.action] [{:keys [money button]} match]
  [:.money] money
  [:button] button)

(declare get-action)
(h/defsnippet event-elem "../resources/public/event.html" [:.event2] [{:keys [name price date author status parts] :as event}]
  [:.name]   (h/content (str author "'s " name))
  [:.date]   (h/content (str date))
  [:.action] (partial action (get-action event)))

(h/defsnippet parts-snip "../resources/public/parts.html" [:#parts-row] [parts]
  [:.parts] (h/set-attr :value (str parts)))

(defn get-button [button-type {:keys [name price date author status parts]}]
  (case button-type
    :pay          (partial actions/pay name date)
    :start        (partial actions/start name date author)
    :participate  (partial actoons/participate name date status))
    :finished     (partial content-wrap "Finished")
  )


(reload/auto-reload *ns*)
