(ns ombs.ddf )

   ;; data depended functions
(defn extract-event [m]
  "Extract event keys from raw result of query participated-list."
  (select-keys m '(:event :remain :price :date)) )
