(ns ombs.view.pages
  (:require
    [net.cgrand.enlive-html :as h]
    [noir.session :as sess]
    [noir.validation :as vld]
    [ombs.core :as core]
    [ombs.view.event :refer [event-elem]]
    ;[noir.util.anti-forgery :refer [anti-forgery-field]] ; security - need, add field fo srcf defence

    ) )

; (defmacro create-error [tag content] `({:tag p :content ("message")}) )

(defmacro defpage [pname rules & body]
  "Marco for declaring pages. Incapsulate validating fields. 
  Redirect on self, when validation fails with attaching errors messages."
  ;validate rules
  ;if ok - body
  ;else - redirect
  )

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
  [:#uname]        (h/content (sess/get :username))
  [:#error]        (h/content (reduce str (map #(str "|" % "|") (vld/get-errors :register))))
  [:#username]     (h/set-attr :value (params :username))
  [:#birthdate]    (h/set-attr :value (params :birthdate))
  [:#student-flag] (if (not-empty (params :student-flag))
                     (h/set-attr :checked "on")  ; check
                     (h/set-attr "" "") )        ; unckeck
  )


(h/deftemplate user "../resources/public/user.html"
  [event-list]
  [:#user] (h/content (sess/get :username))
  [:.debt] (h/content (str (core/debt (sess/get :username))))
  [:main] (h/content (map #(event-elem %) event-list))
  )

