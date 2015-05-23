(ns ombs.view
  (:require 
    [net.cgrand.enlive-html :as h]
    [noir.session :as sess] 
    [noir.validation :as vld]
    ;[noir.util.anti-forgery :refer [anti-forgery-field]] ; security - need, add field fo srcf defence

    ) )

(def event-sel [:#event])
(def participate-button-sel [:#event :> :button])

(h/defsnippet event-elem "../resources/public/event.html" event-sel [{:keys [event remain price username]}]
  [:#ename] (h/content event)
  [:#eprice] (h/content (str price))
  [:#eremain] (h/content (str remain))
  [:#participate] (fn [match] 
                    (if (= (sess/get :username) username) ;if user participated in events
                      ;both should use participate-button-sel
                      ((h/set-attr :style "display: none") match)
                      ((h/set-attr :none "none") match) ) ) )

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
  [:#uname] (h/content (sess/get :username "no user"))
  [:#error] (h/content (str params))
  [:#username]     (h/set-attr :value (params "username"))
  [:#birthdate]    (h/set-attr :value (params "birthdate"))
  [:#student-flag] (if (not-empty (params "student-flag")) 
                     (h/set-attr :checked "on")  ; check
                     (h/set-attr "" "") )        ; unckeck
  )

(h/deftemplate user "../resources/public/user.html" 
  [event-list] 
  [:#user] (h/content (sess/get :username "Anon"))
  [:#event-list] (h/content (map #(event-elem %) event-list))
  )
