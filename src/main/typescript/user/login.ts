interface LoginForm extends HTMLFormElement {
    username: HTMLInputElement;
    password: HTMLInputElement;
}

document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('loginForm') as HTMLFormElement;

    loginForm.addEventListener('submit', (e) => {
        e.preventDefault();

        const username = (document.getElementById('username') as HTMLInputElement);
        const password = (document.getElementById('password') as HTMLInputElement);

        let isValid = true;
        clearErrors();

        if (!username.value.trim()) {
            showError('username', '아이디는 필수 입력 사항입니다.');
            isValid = false;
        }

        if (!password.value.trim()) {
            showError('password', '비밀번호는 필수 입력 사항입니다.');
            isValid = false;
        }

        if (isValid) {
            loginForm.submit();
        }
    });
});

function clearErrors() {
    const errorFields = document.querySelectorAll('.error');
    errorFields.forEach(field => field.classList.remove('error'));
    const errorMessages = document.querySelectorAll('.error-message.show');
    errorMessages.forEach(msg => msg.classList.remove('show'));
}

function showError(fieldId: string, message: string) {
    const field = document.getElementById(fieldId) as HTMLInputElement;
    field.classList.add('error');

    const errorDiv = field.parentElement?.querySelector('.error-message');
    if (errorDiv) {
        errorDiv.textContent = message;
        errorDiv.classList.add('show');
    }
}