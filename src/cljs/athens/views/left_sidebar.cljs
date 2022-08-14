(ns athens.views.left-sidebar
  (:require
    ["/components/Layout/MainSidebar" :refer [MainSidebar]]
    ["/components/Icons/Icons" :refer [DailyNotesIcon AllPagesIcon GraphIcon]]
    ["/components/SidebarShortcuts/List" :refer [List]]
    ["@chakra-ui/react" :refer [Button VStack Flex Heading ButtonGroup Link Flex]]
    [athens.reactive :as reactive]
    [athens.router   :as router]
    [athens.util     :as util]
    [reagent.core    :as r]
    [re-frame.core   :as rf]))


;; Components

(def expanded-sidebar-width "clamp(12rem, 25vw, 18rem)")

(defn route-button
  []
  (fn [is-active? label icon on-click]
    [:> Button {:isActive is-active?
                :textAlign "start"
                :justifyContent "flex-start"
                :variant "ghost"
                :leftIcon icon
                :onClick on-click}
     label]))


(defn left-sidebar
  []
  (let [current-route-name (rf/subscribe [:current-route/name])
        route-name @current-route-name
        shortcuts (reactive/get-reactive-shortcuts)]
    [:> MainSidebar

     [:> VStack {:role "nav" :alignSelf "stretch" :as ButtonGroup :size "sm" :align "stretch" :px 4 :spacing "0.25rem"}
      [route-button (= route-name :home) "Daily Notes" (r/as-element [:> DailyNotesIcon]) (fn [_]
                                                         (rf/dispatch [:reporting/navigation {:source :main-sidebar
                                                                                              :target :home
                                                                                              :pane   :main-pane}])
                                                         (router/nav-daily-notes))]
      [route-button (= route-name :pages) "All Pages" (r/as-element [:> AllPagesIcon]) (fn [_]
                                                        (rf/dispatch [:reporting/navigation {:source :main-sidebar
                                                                                             :target :all-pages
                                                                                             :pane   :main-pane}])
                                                        (router/navigate :pages))]
      [route-button (= route-name :graph) "Graph" (r/as-element [:> GraphIcon]) (fn [_]
                                            (rf/dispatch [:reporting/navigation {:source :main-sidebar
                                                                                 :target :graph
                                                                                 :pane   :main-pane}])
                                            (router/navigate :graph))]]

        ;; SHORTCUTS
     [:> VStack {:as "ol"
                 :align "stretch"
                 :py "1rem"
                 :flex 1
                 :spacing "0.25rem"
                 :overflowY "auto"
                 :backdropFilter "blur(1em)"
                 :borderRadius "lg"
                 :sx {"@supports (overflow-y: overlay)" {:overflowY "overlay"}
                      :listStyle "none"
                      :WebkitAppRegion "no-drag"}}
      [:> Heading {:as "h2"
                   :px 6
                   :pb "0.5rem"
                   :size "sm"
                   :color "foreground.secondary"}
       "Shortcuts"]
      [:> List {:items shortcuts
                :pl 4
                :onOpenItem (fn [e [_order page]]
                              (let [shift? (.-shiftKey e)]
                                (rf/dispatch [:reporting/navigation {:source :left-sidebar
                                                                     :target :page
                                                                     :pane   (if shift?
                                                                               :right-pane
                                                                               :main-pane)}])
                                (router/navigate-page page e)))
                :onUpdateItemsOrder (fn [oldIndex newIndex]
                                      (cond
                                        (< oldIndex newIndex) (rf/dispatch [:left-sidebar/drop oldIndex newIndex :after])
                                        (> oldIndex newIndex) (rf/dispatch [:left-sidebar/drop oldIndex newIndex :before])))}]]

        ;; LOGO + BOTTOM BUTTONS
     [:> Flex {:as "footer"
               :width expanded-sidebar-width
               :flexWrap "wrap"
               :gap "0.25em 0.5em"
               :fontSize "sm"
               :p "2rem"
               :mt "auto"}
      [:> Link {:fontWeight "bold"
                :display "inline-block"
                :href "https://github.com/athensresearch/athens/issues/new/choose"
                :target "_blank"}
       "Athens"]
      [:> Link {:color "foreground.secondary"
                :display "inline-block"
                :href "https://github.com/athensresearch/athens/blob/master/CHANGELOG.md"
                :target "_blank"}
       (util/athens-version)]]]))
