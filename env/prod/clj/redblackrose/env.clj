(ns redblackrose.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[redblackrose started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[redblackrose has shut down successfully]=-"))
   :middleware identity})
