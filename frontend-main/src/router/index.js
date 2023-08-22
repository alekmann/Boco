import { createRouter, createWebHistory } from "vue-router";

const routes = [
  {
    path: "/",
    name: "Home",
    component: () => import("@/views/Home"),
  },
  {
    path: "/signin",
    name: "SignIn",
    component: () => import("@/views/SignIn"),
  },
  {
    path: "/register",
    name: "Register",
    component: () => import("@/views/Register"),
  },
  {
    path: "/faq",
    component: () => import("@/views/FAQ"),
  },
  {
    path: "/activate/:id",
    name: "ActivateUser",
    component: () => import("@/views/ActivateUser"),
  },
  {
    path: "/fill",
    name: "Fill",
    component: () => import("@/views/Fill.vue"),
  },
  {
    path: "/posts/:id",
    name: "PostView",
    component: () => import("@/views/PostView"),
  },
  {
    path: "/userProfile/:id",
    name: "UserProfile",
    component: () => import("@/views/TheirProfile"),
  },
  {
    path: "/orgProfile/:id",
    name: "OrganizationProfile",
    component: () => import("@/views/TheirProfile"),
  },

  {
    path: "/myProfile",
    component: () => import("@/views/MyProfile"),
    children: [
      { path: "", component: () => import("@/components/profile/Posts") },
      {
        path: "applications",
        component: () => import("@/components/profile/Applications"),
      },
      {
        path: "theirApplications",
        component: () => import("@/components/profile/TheirApplications"),
      },
      {
        path: "administrateuser",
        component: () => import("@/components/profile/AdministrateUser"),
      },
      {
        path: "organization",
        component: () => import("@/components/profile/Organization"),
      },
    ],
  },

  {
    path: "/myOrganization",
    component: () => import("@/views/MyOrganization"),
    children: [
      { path: "", component: () => import("@/components/profile/Posts") },
      {
        path: "applications",
        component: () => import("@/components/profile/Applications"),
      },
      {
        path: "theirApplications",
        component: () => import("@/components/profile/TheirApplications"),
      },
      {
        path: "administrateorg",
        component: () => import("@/components/profile/AdministrateOrg"),
      },
      {
        path: "members",
        component: () => import("@/components/profile/Members"),
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  linkActiveClass: "active",
  routes,
});

export default router;
