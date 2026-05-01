const API_BASE_URL = '';  // Relative path, assumes frontend and backend are served together(Dynamic Portability)

class ApiService {
  static getHeaders() {
    return {
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    };
  }

  static async request(endpoint, method = 'GET', body = null) {
    const options = {
      method,
      headers: this.getHeaders(),
      credentials: 'same-origin',
      cache: 'no-store'
    };
    
    if (body) {
      options.body = JSON.stringify(body); //JSON.stringify(): Converts a JavaScript object to text suitable for network travel.
    }

    try {

      const response = await fetch(`${API_BASE_URL}${endpoint}`, options);

      if (!response.ok) {
        let errorData;

        try {
         errorData = await response.json(); } catch (e) { errorData = null; }
        
        /*
         * Handle Spring Boot validation errors which often return as an array.
         * We extract all the 'message' fields, filter out nulls, and join them into a single string.
         */
        if (Array.isArray(errorData)) {
          const messages = errorData.map(e => e.message).filter(Boolean).join('; ');
          throw new Error(messages || `HTTP error! status: ${response.status}`);
        }
        throw new Error((errorData && errorData.message) ? errorData.message : `HTTP error! status: ${response.status}`);
      }

      if (response.status === 204) return null; //HTTP 204 means 'No Content'.


      const contentType = response.headers.get('content-type') || '';
      const text = await response.text();
      if (!text || !contentType.includes('application/json')) {
        return null;  // Safe fallback
      }
      return JSON.parse(text);
    } catch (error) {
      console.error(`API Request failed for ${endpoint}:`, error);
      throw error;
    }
  }


  static get(endpoint) {
    return this.request(endpoint, 'GET');
  }


  static post(endpoint, body) {
    return this.request(endpoint, 'POST', body);
  }


  static put(endpoint, body) {
    return this.request(endpoint, 'PUT', body);
  }


  static delete(endpoint) {
    return this.request(endpoint, 'DELETE');
  }
}
