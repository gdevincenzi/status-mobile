(ns status-im2.setup.core
  (:require [re-frame.core :as re-frame]
            [re-frame.interop :as interop]
            [reagent.impl.batching :as batching]
            [react-native.core :as rn]
            [react-native.platform :as platform]
            [react-native.shake :as react-native-shake]
            [react-native.languages :as react-native-languages]

            [i18n.i18n :as i18n]

            status-im2.setup.events
            status-im2.contexts.syncing.events
            status-im2.subs.root
            status-im2.navigation.core

            [status-im2.setup.dev :as dev]
            [status-im2.setup.global-error :as global-error]
            [status-im2.setup.i18n-resources :as i18n-resources]
            [status-im2.setup.config :as config]
            [status-im2.setup.log :as log]
            [status-im2.contexts.shell.animation :as animation]

            ;; TODO (14/11/22 flexsurfer move to status-im2 namespace
            status-im.events
            [status-im.native-module.core :as status]
            [status-im.notifications.local :as notifications]
            [status-im.async-storage.core :as async-storage]
            [status-im.utils.universal-links.core :as utils.universal-links]))

;;;; re-frame RN setup
(set! interop/next-tick js/setTimeout)
(set! batching/fake-raf #(js/setTimeout % 0))

(defn init []
  (log/setup config/log-level)
  (global-error/register-handler)
  (when platform/android?
    (status/set-soft-input-mode status/adjust-resize))
  (notifications/listen-notifications)
  (.addEventListener rn/app-state "change" #(re-frame/dispatch [:app-state-change %]))
  (i18n/init (name i18n-resources/default-device-language) (clj->js i18n-resources/translations-by-locale))
  (react-native-languages/add-change-listener #(fn [lang]
                                                 (i18n/set-language lang)
                                                 (i18n-resources/load-language lang)))
  (react-native-shake/add-shake-listener #(re-frame/dispatch [:shake-event]))
  (utils.universal-links/initialize)

  ;; TODO(parvesh) - Remove while moving functionality to status-go
  (async-storage/get-item :selected-stack-id #(animation/selected-stack-id-loaded %))

  (dev/setup)

  (re-frame/dispatch-sync [:setup/app-started]))
