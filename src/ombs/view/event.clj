(ns ombs.view.event
  (:require 
    [net.cgrand.enlive-html :as h]
    [noir.session :as sess]
    [noir.validation :as vld]  
    [ombs.core :as core]
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
  [:#error] (h/content (reduce str (map #(str "|" % "|") (vld/get-errors :event))))
  )

(def event-sel [:article])

(h/defsnippet event-elem "../resources/public/event.html" event-sel 
  [{{:keys [event price date]}  :event 
           users                :users 
    :as events}]
  [:#name]   (h/set-attr :value event)
  [:#date]   (h/set-attr :value date)
  [:#price]  (h/set-attr :value (str price))
  [:#debt]   (h/set-attr :value (core/debt (sess/get :username) event date))
  [:.action.participate] (fn [match]
               (if (core/need-button? (sess/get :username) events)  ;if user participated in events
                 ((h/remove-attr :disabled "")  match)
                 ((h/set-attr :disabled "")     match)      
                 ))
  [:.action.pay] (fn [match]
                   (if (and 
                         (not= (core/debt (sess/get :username) event date) 0.0) ; have debt
                         (not (core/need-button? (sess/get :username) events)) ;i'm stake in event
                         )
                     ((h/remove-attr :disabled "")  match)
                     ((h/set-attr :disabled "")     match)      

                     )
                   )
  
  )
