(ns ombs.view.event
  (:require
    [net.cgrand.enlive-html :as h]
    [net.cgrand.reload :as reload]
    [noir.session :as sess]
    [noir.validation :as vld]
    [ombs.core :as core]
    [ombs.validate :refer [errors-string]]
    ))

(declare get-action)
(declare get-money)
(def event-sel [:.event2])
(h/defsnippet event-elem "../resources/public/event.html" event-sel [{:keys [name price date author status parts] :as event}]
  [:.name]   (h/content (str author "'s " name))
  [:.date]   (h/content (str date))
  [:.author] (h/content (str "Author: " author))
  [:.money]  (h/content (str (get-money event)))
  [:.action] (get-action event)
  ;[:.price] (h/content (str price))
  ;[:#parts-row]
  ;[:.action.participate]
  ;[:.action.start]
  )

(def parts-row-sel [:#parts-row]) ;tag in parts.html
(h/defsnippet parts-snip "../resources/public/parts.html" parts-row-sel [parts]
  [:.parts] (h/set-attr :value (str parts)))

(declare start-action)
(declare fill-parts)
(declare participate-action)
(declare pay-action)
(defn get-action [{:keys [name price date author status parts]}]
  (case 1
    1 (partial pay-action name date)
    2 (partial fill-parts parts)
    3 (partial start-action name date author)
    4 (partial participate-action name date status)
    )
  )

(defn get-money [{:keys [name price date author status parts]}]
  0
  )

(defn set-attr-class
  ([attr]
    (set-attr-class attr ""))
  ([attr value]
    {:pre (= (type attr) java.lang.String)}
    (comp (h/set-attr attr value) (h/add-class attr))) )

(defn rm-attr-class [attr]
  {:pre (= (type attr) java.lang.String)}
  (comp (h/remove-attr attr ) (h/remove-class attr)) )

(defn pay-action [name date match]
  (if (and
        (core/participated? (sess/get :username) name date)
        (core/is-active? name date) ) ; have debt

    ((h/content "Pay") ((rm-attr-class "disabled")   match))
    ((h/content "Pay") ((set-attr-class "disabled")  match))
    ))

(defn start-action [name date author match]
  (if (and (= author (sess/get :username))
           (core/is-initial? name date)
           (> (core/participants-count name date) 0))
    ((h/remove-class "disabled") match)
    ((h/add-class "disabled")    match)))

(defn participate-action [name date status match]
  (if (and
        (not (core/participated? (sess/get :username) name date))
        (not= status "finished"))
    ((h/remove-attr "disabled") match)
    ((h/add-class "disabled")   match)))

(defn fill-parts [parts match]
  (if (= 0 parts)
    nil
    ((h/content (parts-snip parts)) match)))


(reload/auto-reload *ns*)
