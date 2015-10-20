(ns ombs.view.event
  (:require
    [net.cgrand.enlive-html :as h]
    [net.cgrand.reload :as reload]
    [noir.session :as sess]
    [noir.validation :as vld]
    [ombs.core :as core]
    [ombs.validate :refer [errors-string]]
    ))

(declare start-action)
(declare fill-parts)
(declare participate-action)
(declare pay-action)
(def event-sel [:.event])
(h/defsnippet event-elem "../resources/public/event.html" event-sel [{:keys [name price date author status parts]}]
  [:.name]              (h/set-attr :value name)
  [:.date]              (h/set-attr :value date)
  [:.author]            (h/set-attr :value author)
  [:.price]             (h/set-attr :value (str price))
  [:.debt]              (h/set-attr :value (core/debt (sess/get :username) name date))
  [:#parts-row]         (partial fill-parts parts)
  [:.action.participate](partial participate-action name date status)
  [:.action.pay]        (partial pay-action name date)
  [:.action.start]      (partial start-action name date author)
  )

(def parts-row-sel [:#parts-row]) ;tag in parts.html
(h/defsnippet parts-snip "../resources/public/parts.html" parts-row-sel [parts]
  [:.parts] (h/set-attr :value (str parts)))

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
    ((rm-attr-class "disabled")   match)
    ((set-attr-class "disabled")  match)
    ))

(defn start-action [name date author match]
  (if (and (= author (sess/get :username))
           (core/is-initial? name date)
           (> (core/participants-count name date) 0))
    ((rm-attr-class "disabled")   match)
    ((set-attr-class "disabled")  match)
    ))

(defn participate-action [name date status match]
  (if (and
        (not (core/participated? (sess/get :username) name date))
        (not= status "finished"))
    ((rm-attr-class "disabled")   match)
    ((set-attr-class "disabled")  match)
    ))

(defn fill-parts [parts match]
  (if (= 0 parts)
    nil
    ((h/content (parts-snip parts)) match)))


(reload/auto-reload *ns*)
