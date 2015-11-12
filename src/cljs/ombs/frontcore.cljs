(ns ombs.frontcore
  (:require
	[om.core :as om]
	[om-tools.dom :as dom]
	))


(defn mycomponent
  [app owner]
  (reify
    ;; Set the initial component state.
    om/IInitState
    (init-state [_]
      {:message "Hello world from local state"})

    ;; Render the component with current local state.
    om/IRenderState
    (render-state [_ {:keys [message]}]
      (dom/section
        (dom/div message)
        (dom/div (:message app))))))

(defonce state {:message "Hello world from global state."})

;; "app" is the id of a dom element in index.html
(let [el (js/document.getElementById "app")]
  (om/root mycomponent state {:target el}))


; (defn set-html! [el content]
;   (set! (.-innerHTML el) content))
;
; (defn main []
;   (let [content "HEllo, CLJS!"
;         element (aget (js/document.getElementsByTagName "main") 0)]
;     (set-html! element content)))
; (main)
