(ns status-im2.common.confirmation-drawer.view
  (:require [reagent.core :as reagent]
            [i18n.i18n :as i18n]
            [utils.re-frame :as rf]
            [react-native.core :as rn]
            [quo2.core :as quo]
            [status-im2.common.confirmation-drawer.style :as style]))

(defn avatar [group-chat color display-name photo-path]
  (if group-chat
    [quo/group-avatar {:color color
                       :size  :small}]
    [quo/user-avatar {:full-name        display-name
                      :profile-picture  photo-path
                      :size             :xxs
                      :status-indicator false}]))

(defn extra-action-view [extra-action extra-text extra-action-selected?]
  (when extra-action
    [rn/view {:style {:margin-top 16 :flex-direction :row}}
     [quo/checkbox {:on-change (fn [selected?] (reset! extra-action-selected? selected?))}]
     [quo/text {:style {:margin-left 10}} extra-text]]))

(defn confirmation-drawer [{:keys [title description context button-text on-press extra-action extra-text]}]
  (let [extra-action-selected? (reagent/atom false)]
    (fn []
      (let [{:keys [group-chat chat-id public-key color name]} context
            id           (or chat-id public-key)
            display-name (if-not group-chat (first (rf/sub [:contacts/contact-two-names-by-identity id])) name)
            contact      (when-not group-chat (rf/sub [:contacts/contact-by-address id]))
            photo-path   (when-not (empty? (:images contact)) (rf/sub [:chats/photo-path id]))]
        [rn/view {:style {:margin-horizontal 20}}
         [quo/text {:weight :semi-bold
                    :size   :heading-1} title]
         [rn/view {:style (style/context-container)}
          [avatar group-chat color display-name photo-path]
          [quo/text {:weight :medium
                     :size   :paragraph-2
                     :style  {:margin-left 4}} display-name]]
         [quo/text description]
         [extra-action-view extra-action extra-text extra-action-selected?]
         [rn/view {:style style/buttons-container}
          [quo/button {:type     :grey
                       :style    {:flex 0.48} ;;WUT? 0.48 , whats that ?
                       :on-press #(rf/dispatch [:bottom-sheet/hide])}
           (i18n/label :t/close)]
          [quo/button {:type     :danger
                       :style    {:flex 0.48}
                       :on-press #(do
                                    (when @extra-action-selected? (extra-action))
                                    (on-press))}
           button-text]]]))))
