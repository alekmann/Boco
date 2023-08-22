import http from "@/service/http";

export const createOrganization = (organization) => {
  return http.post("/organizations", organization);
};

export const editOrganization = (organization) => {
  return http.put("/organizations", organization);
};

export const changePicture = (orgNum, picture) => {
  return http.patch(`/organizations/${orgNum}/picture`, picture);
};

export const getOrganizationInfo = (id) => {
  return http.get("/organizations/" + id);
};

export const getOrgName = (orgNum) => {
  return http.get(`/organizations/${orgNum}/name`);
};

export const addNewMember = (NewMember) => {
  return http.post("/employees", NewMember);
};

export const getMembers = (orgNum) => {
  return http.get(`/organizations/${orgNum}/employees`);
};

// Used to search for employees.
export const search = (orgNum, searchRequest) => {
  return http.get(
    `/organizations/${orgNum}/employees/search?search=${searchRequest}`
  );
};

export const deleteOrg = (orgNum) => {
  return http.delete(`/organizations/${orgNum}`);
};

export const deleteMember = (orgNum, id) => {
  return http.delete(`/organizations/${orgNum}/employees/${id}`);
};
