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


(def event-sel [:#event])
;(def participate-button-sel [:#event :> :button])

(h/defsnippet event-elem "../resources/public/event.html" event-sel [{{:keys [event price remain date]} :event users :users :as event-user}]
  [:#ename] (h/content event)
  [:#eprice] (h/content (str price))
  [:#eremain] (h/content (str remain))
  [:#participate] (fn [match]
                    ;(println (str "list:" users "session user:" (sess/get :username) "need btn:" (core/need-button? (sess/get :username) users)))
                    (if (core/need-button? (sess/get :username) event-user)  ;if user participated in events
                      ;both should use participate-button-sel
                      ((h/set-attr :none "none") match)      
                      ((h/set-attr :style "display: none") match)
                      )))


(h/deftemplate user "../resources/public/user.html"
  [event-list]
  [:#user] (h/content (sess/get :username))
  [:#event-list] (h/content (map #(event-elem %) event-list))
  )
