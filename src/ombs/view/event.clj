(ns ombs.view.event
  (:require 
    [net.cgrand.enlive-html :as h]
    [noir.session :as sess]
    [noir.validation :as vld]  
    [ombs.core :as core]
    [clj-time.format :refer [formatter unparse]]
    [clj-time.local :refer [local-now]]
    [clj-time.core :refer [date-time]]
    ))

; Helpers, for creation list of user for participation
(def usercheckbox-sel [:.users] )
(h/defsnippet usercheckbox-elem "../resources/public/addevent.html" usercheckbox-sel [{username :name}] 
  [:.userbox] (h/do-> 
                (h/content username)  
                (h/set-attr :value username)))

(h/deftemplate addevent-page "../resources/public/addevent.html"
  [users]
  [:.users] (h/content ( map #(usercheckbox-elem %) users) )
  [:#edate] (h/set-attr :value (unparse (formatter "YYYY-MM-dd") (local-now) ) )
  [:#error] (h/content (reduce str (map #(str "|" % "|") (vld/get-errors :event))))
  )

(def event-sel [:.event])

(h/defsnippet event-elem "../resources/public/event.html" event-sel 
  [{{:keys [ename price date author]}  :event 
           users                :users 
    :as event}]
  [:.name]   (h/set-attr :value ename)
  [:.date]   (h/set-attr :value date)
  [:.author] (h/set-attr :value author)
  [:.price]  (h/set-attr :value (str price))
  [:.debt]   (h/set-attr :value (core/debt (sess/get :username) ename date))
  [:.action.participate] (fn [match]
               (if (core/need-button? (sess/get :username) event)  ;if user participated in event
                 ((h/remove-attr :disabled "")  match)
                 ((h/set-attr :disabled "")     match)))
  [:.action.pay] (fn [match]
                   (if (and 
                         (not= (core/debt (sess/get :username) ename date) 0.0) ; have debt
                         (not (core/need-button? (sess/get :username) event)) ;i'm stake in ename
                         )
                     ((h/remove-attr :disabled "")  match)
                     ((h/set-attr :disabled "")     match)))
  [:.action.start] (fn [match]
                     (if (and (= author (sess/get :username)) (core/is-initial? ename date))
                       ((h/remove-attr :disabled "")  match)
                       ((h/set-attr :disabled "")     match)))
  
  )
