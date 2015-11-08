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
(h/defsnippet action "../resources/public/action.html" [:.action] [event]
  [:.money] (get-money event)
  [:button] (get-action event))

(h/defsnippet event-elem "../resources/public/event.html" [:.event2] [{:keys [name price date author status parts] :as event}]
  [:.name]   (h/content (str author "'s " name))
  [:.date]   (h/content (str date))
  [:.action] (h/content (action event)))


(def parts-row-sel [:#parts-row]) ;tag in parts.html
(h/defsnippet parts-snip "../resources/public/parts.html" parts-row-sel [parts]
  [:.parts] (h/set-attr :value (str parts)))

(defn content-wrap [value match] ((h/content (str value)) match))
(defn get-money [{:keys [name price date author status parts]}]
  "Return price or user debt, depends on user participation. 
  If user not participate - show price; else - show debt. 
  Also, if user have fee on this event, or event is finished - return nil"
  (when-let [uname (sess/get :username)]
    (if (and
          (core/participated? uname name date)
          (core/is-active? name date) ) ; have debt
      (partial content-wrap (core/debt uname name date))
      (partial content-wrap price)
      )))

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

(defn get-action [{:keys [name price date author status parts]}]
  (case 1
    1 (partial pay-action name date)
    2 (partial fill-parts parts)
    3 (partial start-action name date author)
    4 (partial participate-action name date status)
    )
  )


(reload/auto-reload *ns*)
