(ns ombs.view
  (:require
    [net.cgrand.enlive-html :as h]
    [noir.session :as sess]
    [noir.validation :as vld]
    [ombs.core :as core]
    ;[noir.util.anti-forgery :refer [anti-forgery-field]] ; security - need, add field fo srcf defence

    ) )

(defmacro create-error [tag content] `({:tag p :content ("message")}) )

(h/deftemplate index "../resources/public/index.html" [& ctxt]
  ;[:#ename] (fn [match]
  ;  (vld/on-error :ename ((h/set-attr :placeholder (vld/get-errors :ename)) match) ))
  [:#error] (h/content (reduce str (map #(str "|" % "|") (vld/get-errors))))
                ;(if-let [error (:error ctxt)]
                ;     ((h/content error) match)
                ;     ((h/content "") match) )

  ; hide log and reg forms, show logout form if have username in session
  ;[:#logform] (hide)
  ;[:#regform] (hide)
  ;[:#logout]  (unhide)
  )

;Generate register page. If in given params founded keys for this page - fill fields with founded values
(h/deftemplate register "../resources/public/register.html" [params]
  [:#uname] (h/content (sess/get :username))
  [:#error] (h/content (str params))
  [:#username]     (h/set-attr :value (params "username"))
  [:#birthdate]    (h/set-attr :value (params "birthdate"))
  [:#student-flag] (if (not-empty (params "student-flag"))
                     (h/set-attr :checked "on")  ; check
                     (h/set-attr "" "") )        ; unckeck
  )


(def event-sel [:article])
;(def participate-button-sel [:#event :> :button])

(h/defsnippet event-elem "../resources/public/event.html" event-sel [{{:keys [event price remain date]} :event users :users :as event-user-list}]
  [:#ename] (h/set-attr :value event)
  [:#eprice] (h/set-attr :value (str price))
  [:#edebt] (h/set-attr :value 0)
  [:#action] (fn [match]
                    (if (core/need-button? (sess/get :username) event-user-list)  ;if user participated in events
                      ((h/remove-attr :disabled "")  match)
                      ((h/set-attr :disabled "")     match)      
                      ) )
)


(h/deftemplate user "../resources/public/user.html"
  [event-list]
  [:#user] (h/content (sess/get :username))
  [:main] (h/content (map #(event-elem %) event-list))
  )
