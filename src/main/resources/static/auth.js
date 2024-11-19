const API_URL = 'http://localhost:8080/api/auth';

export function setAuthHeader() {
    const token = localStorage.getItem('jwtToken');
    return token ? { Authorization: 'Bearer ' + token } : {};
}

export async function login(email, password) {
    try {
        const response = await fetch(`${API_URL}/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ email, password }),
        });

        if (response.ok) {
            const data = await response.json();
            localStorage.setItem('jwtToken', data.token); // Store the token
            return { success: true, message: 'Login successful!' };
        } else {
            const errorData = await response.json();
            return { success: false, message: errorData.error };
        }
    } catch (error) {
        console.error('Error during login:', error);
        return { success: false, message: 'Login failed. Please try again.' };
    }
}

export async function register(userDto) {
    const formData = new FormData();

    // Append all the userDto properties to the FormData object
    for (const key in userDto) {
        formData.append(key, userDto[key]);
    }

    try {
        const response = await fetch(`${API_URL}/register`, {
            method: 'POST',
            body: formData, // Send form data including the file
        });

        if (response.ok) {
            const data = await response.json();
            return { success: true, message: 'Registration successful!' };
        } else {
            const errorData = await response.json();
            return { success: false, message: errorData.error || 'Registration failed.' };
        }
    } catch (error) {
        console.error('Error during registration:', error);
        return { success: false, message: 'Registration failed. Please try again.' };
    }
}


