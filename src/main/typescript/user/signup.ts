document.addEventListener('DOMContentLoaded', () => {
    const loginIdInput = document.getElementById('loginId') as HTMLInputElement;
    const passwordInput = document.getElementById('password') as HTMLInputElement;
    const confirmPasswordInput = document.getElementById('confirmPassword') as HTMLInputElement;
    const usernameInput = document.getElementById('username') as HTMLInputElement;
    const emailInput = document.getElementById('email') as HTMLInputElement;

    // 로그인 ID 중복 체크
    loginIdInput.addEventListener('blur', async () => {
        if (!loginIdInput.value.trim()) return;

        try {
            const response = await fetch(`/api/user/check-loginId?loginId=${encodeURIComponent(loginIdInput.value)}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) {
                throw new Error('Network response was not ok');
            }

            const data = await response.json();
            const messageDiv = loginIdInput.parentElement?.querySelector('.message');

            if (data.available) {
                loginIdInput.classList.remove('error');
                loginIdInput.classList.add('success');
                if (messageDiv) {
                    messageDiv.textContent = data.message;
                    messageDiv.classList.remove('error');
                    messageDiv.classList.add('success');
                }
            } else {
                loginIdInput.classList.remove('success');
                loginIdInput.classList.add('error');
                if (messageDiv) {
                    messageDiv.textContent = data.message;
                    messageDiv.classList.remove('success');
                    messageDiv.classList.add('error');
                }
            }
        } catch (error) {
            console.error('Error checking login ID:', error);
        }
    });

    // 비밀번호 확인 체크
    confirmPasswordInput.addEventListener('blur', () => {
        if (!passwordInput.value.trim()) return;

        const passwordMessageDiv = passwordInput.parentElement?.querySelector('.message');
        const confirmMessageDiv = confirmPasswordInput.parentElement?.querySelector('.message');
        const isMatch = passwordInput.value === confirmPasswordInput.value;

        [passwordInput, confirmPasswordInput].forEach(input => {
            input.classList.remove('error', 'success');
            input.classList.add(isMatch ? 'success' : 'error');
        });

        if (passwordMessageDiv) {
            passwordMessageDiv.textContent = '';
        }

        if (confirmMessageDiv) {
            confirmMessageDiv.textContent = isMatch ? '비밀번호가 일치합니다.' : '비밀번호가 일치하지 않습니다.';
            confirmMessageDiv.classList.remove('error', 'success');
            confirmMessageDiv.classList.add(isMatch ? 'success' : 'error');
        }
    });

    // 사용자 이름 체크
    usernameInput.addEventListener('blur', () => {
        if (!usernameInput.value.trim()) return;

        usernameInput.classList.remove('error');
        usernameInput.classList.add('success');
        const messageDiv = usernameInput.parentElement?.querySelector('.message');
        if (messageDiv) {
            messageDiv.textContent = '';
        }
    });

    // 이메일 유효성 체크
    emailInput.addEventListener('blur', () => {
        if (!emailInput.value.trim()) return;

        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        const isValid = emailRegex.test(emailInput.value);
        const messageDiv = emailInput.parentElement?.querySelector('.message');

        emailInput.classList.remove('error', 'success');
        emailInput.classList.add(isValid ? 'success' : 'error');

        if (messageDiv) {
            messageDiv.textContent = isValid ? '' : '올바른 이메일 형식이 아닙니다.';
            messageDiv.classList.remove('success', 'error');
            if (!isValid) messageDiv.classList.add('error');
        }
    });
});