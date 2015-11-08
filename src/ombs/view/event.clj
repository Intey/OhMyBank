(ns ombs.view.event
  (:require
    [net.cgrand.enlive-html :as h]
    [net.cgrand.reload :as reload]
    [noir.session :as sess]
    [noir.validation :as vld]
    [ombs.core :as core]
    [ombs.validate :refer [errors-string]]
    [ombs.view.dom :refer [set-attr-class rm-attr-class content-wrap]]
    ))

(h/defsnippet action "../resources/public/action.html" [:.action] [{:keys [money button]} match]
  [:.money] money
  [:button] button)

(declare get-action)
(declare get-button)

(h/defsnippet event-elem "../resources/public/event.html" [:.event2] [{:keys [name price date author status parts] :as event}]
  [:.name]   (h/content (str author "'s " name))
  [:.date]   (h/content (str date))
  [:.action] (partial action (get-action event)))

(h/defsnippet parts-snip "../resources/public/parts.html" [:#parts-row] [parts]
  [:.parts] (h/set-attr :value (str parts)))


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

(defn fill-parts [parts match]
  (if (= 0 parts)
    nil
    ((h/content (parts-snip parts)) match)))

(defn pay-action [name date match]
  (if (and
        (core/participated? (sess/get :username) name date)
        (core/is-active? name date) ) ; have debt
    ; add parts field
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

(defn get-action [event]
  "return hashmap with function for money and button"
  {:money (get-money event)
   :button (get-button :pay event) }
  )

(defn get-button [button-type {:keys [name price date author status parts]}]
  (case button-type
    :pay          (partial pay-action name date)
    :start        (partial start-action name date author)
    :participate  (partial participate-action name date status))
  )


(reload/auto-reload *ns*)
