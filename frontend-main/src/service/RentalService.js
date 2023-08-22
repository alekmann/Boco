import http from "./http";

export const createRentalRequest = (postId, rentalRequest) => {
  return http.post(`/posts/${postId}/rentals`, rentalRequest);
};

export const getRentalRequests = (approved) => {
  if (approved !== undefined) {
    return http.get(`/rentals?approved=${approved}`);
  }
  return http.get("/rentals");
};

export const answerRequest = (id, answer) => {
  return http.patch(`/rentals/${id}/approved`, { approved: answer });
};

export const deleteRequest = (id) => {
  return http.delete(`/rentals/${id}`);
};

// Collects the rentals of an organization or person.
export const getRentals = (organization) => {
  if (organization) {
    return http.get("/rentals?organiation=" + organization.orgNum);
  }
  return http.get(`/rentals`);
};

export const getTheirRentals = (organization) => {
  if (organization) {
    return http.get("/theirRentals?organization=" + organization.orgNum);
  }
  return http.get(`/theirRentals`);
};

export const getRentalRequestsCount = () => {
  return http.get("/rentals/count");
};
