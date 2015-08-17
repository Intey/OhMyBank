(ns ombs.view.pages
  (:require
    [net.cgrand.enlive-html :as h]
    [noir.session :as sess]
    [ombs.validate :refer [errors-string]]
    [ombs.core :as core]
    [ombs.view.event :refer [event-elem]]
    [clj-time.format :refer [formatter unparse]]
    [clj-time.local :refer [local-now]]
    [clj-time.core :refer [date-time]]
    ;[noir.util.anti-forgery :refer [anti-forgery-field]] ; security - need, add field fo srcf defence

    ) )

; ============================= index page ================================

(h/deftemplate index "../resources/public/index.html" []
  [:#error] (h/content (errors-string))
  ; hide log and reg forms, show logout form if have username in session
  ;[:#logform] (hide)
  ;[:#regform] (hide)
  ;[:#logout]  (unhide)
  )

; ============================= register page ================================

;Generate register page. If in given params founded keys for this page - fill fields with founded values
(h/deftemplate register "../resources/public/register.html" [params]
  [:#uname]        (h/content (sess/get :username))
  [:#error]        (h/content (errors-string :register))
  [:#username]     (h/set-attr :value (params :username))
  [:#birthdate]    (h/set-attr :value (params :birthdate))
  [:#student-flag] (if (not-empty (params :student-flag))
                     (h/set-attr :checked "on")  ; check
                     (h/set-attr "" "") )        ; unckeck
  )

; ============================= user page ================================

(h/deftemplate user "../resources/public/user.html" [username]
  [:#user] (h/content username)
  [:#error] (h/content (errors-string [:participation :pay]))
  [:.debt] (h/content (str (core/debt username)))
  [:section.events :> :article] (h/content (map #(event-elem %) (core/events)))
  )

; ============================= add event page ================================

; Helpers, for creation list of user for participation
(def usercheckbox-sel [:.users] )

(h/defsnippet usercheckbox-elem "../resources/public/addevent.html" usercheckbox-sel [{username :name}] 
  [:.userbox] (h/do-> 
                (h/content username)  
                (h/set-attr :value username)))

(h/deftemplate addevent "../resources/public/addevent.html"
  [users]
  [:.users] (h/content ( map #(usercheckbox-elem %) users) )
  [:#edate] (h/set-attr :value (unparse (formatter "YYYY-MM-dd") (local-now) ) )
  [:#error] (h/content (errors-string :event))
  )
