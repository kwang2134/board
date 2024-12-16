interface HTMLElementEvent extends Event {
    target: HTMLElement;
}

document.addEventListener('DOMContentLoaded', () => {
    const tabs = document.querySelectorAll('.tab');
    const fragments = document.querySelectorAll('.fragment');
    const postsFragment = document.getElementById('posts-fragment');
    const passwordInput = document.getElementById('password') as HTMLInputElement;
    const confirmPasswordInput = document.getElementById('confirmPassword') as HTMLInputElement;
    const emailInput = document.querySelector('input[type="email"]') as HTMLInputElement;
    const usernameInput = document.querySelector('input[name="username"]') as HTMLInputElement;
    const editForm = document.getElementById('editForm') as HTMLFormElement;

    // username 유효성 검사
    usernameInput?.addEventListener('input', () => {
        const messageDiv = usernameInput.parentElement?.querySelector('.message');

        if (!usernameInput.value.trim()) {
            usernameInput.classList.remove('success');
            usernameInput.classList.add('error');
            if (messageDiv) {
                messageDiv.textContent = '필수 입력 사항입니다.';
                messageDiv.classList.add('error');
            }
        } else {
            usernameInput.classList.remove('error');
            usernameInput.classList.add('success');
            if (messageDiv) {
                messageDiv.textContent = '';
                messageDiv.classList.remove('error');
            }
        }
    });

    usernameInput?.addEventListener('blur', () => {
        if (!usernameInput.value.trim()) {
            usernameInput.classList.remove('success');
            usernameInput.classList.add('error');
            const messageDiv = usernameInput.parentElement?.querySelector('.message');
            if (messageDiv) {
                messageDiv.textContent = '필수 입력 사항입니다.';
                messageDiv.classList.add('error');
            }
        }
    });

    // 이메일 유효성 검사
    emailInput?.addEventListener('input', () => {
        const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
        const messageDiv = emailInput.parentElement?.querySelector('.message');

        if (emailInput.value.trim() === '') {
            emailInput.classList.remove('error', 'success');
            if (messageDiv) {
                messageDiv.textContent = '';
                messageDiv.classList.remove('error', 'success');
            }
            return;
        }

        const isValid = emailRegex.test(emailInput.value);
        emailInput.classList.remove('error', 'success');
        emailInput.classList.add(isValid ? 'success' : 'error');

        if (messageDiv) {
            messageDiv.textContent = isValid ? '' : '올바른 이메일 형식이 아닙니다.';
            messageDiv.classList.remove('error', 'success');
            if (!isValid) messageDiv.classList.add('error');
        }
    });

    // 비밀번호 확인 체크
    const checkPasswordMatch = () => {
        if (!passwordInput?.value.trim() || !confirmPasswordInput?.value.trim()) return;

        const messageDiv = confirmPasswordInput.parentElement?.querySelector('.message');
        const isMatch = passwordInput.value === confirmPasswordInput.value;

        [passwordInput, confirmPasswordInput].forEach(input => {
            if (input) {
                input.classList.remove('error', 'success');
                input.classList.add(isMatch ? 'success' : 'error');
            }
        });

        if (messageDiv) {
            messageDiv.textContent = isMatch ? '비밀번호가 일치합니다.' : '비밀번호가 일치하지 않습니다.';
            messageDiv.classList.remove('error', 'success');
            messageDiv.classList.add(isMatch ? 'success' : 'error');
        }
    };

    passwordInput?.addEventListener('input', checkPasswordMatch);
    confirmPasswordInput?.addEventListener('input', checkPasswordMatch);

    // 폼 제출 전 유효성 검사
    editForm?.addEventListener('submit', (e) => {
        const passwordMatch = passwordInput?.value === confirmPasswordInput?.value;
        const emailValid = !emailInput?.value.trim() ||
            /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(emailInput.value);
        const usernameValid = usernameInput?.value.trim() !== '';

        if (!passwordMatch || !emailValid || !usernameValid) {
            e.preventDefault();
            if (!passwordMatch) {
                confirmPasswordInput?.classList.add('error');
            }
            if (!emailValid) {
                emailInput?.classList.add('error');
            }
            if (!usernameValid) {
                usernameInput?.classList.add('error');
                const messageDiv = usernameInput?.parentElement?.querySelector('.message');
                if (messageDiv) {
                    messageDiv.textContent = '필수 입력 사항입니다.';
                    messageDiv.classList.add('error');
                }
            }
        }
    });

    // 페이지 번호 클릭 이벤트 처리 함수
    const handlePageClick = async (e: MouseEvent) => {
        const target = e.target as HTMLElement;
        if (target && target.matches('.page-number, .page-button')) {
            e.preventDefault();
            try {
                const link = target as HTMLAnchorElement;
                const response = await fetch(link.href);
                if (!response.ok) throw new Error('Network response was not ok');
                const html = await response.text();
                if (postsFragment) {
                    const tempDiv = document.createElement('div');
                    tempDiv.innerHTML = html;
                    const newPostsFragment = tempDiv.querySelector('#posts-fragment');
                    if (newPostsFragment) {
                        postsFragment.innerHTML = newPostsFragment.innerHTML;
                    }
                }
            } catch (error) {
                console.error('Error loading page:', error);
            }
        }
    };

    // 페이지네이션 이벤트 리스너 등록
    document.addEventListener('click', handlePageClick);

    // 탭 전환 이벤트
    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            const tabId = tab.getAttribute('data-tab');

            tabs.forEach(t => t.classList.remove('active'));
            tab.classList.add('active');

            fragments.forEach(fragment => {
                if (fragment.id === `${tabId}-fragment`) {
                    fragment.classList.add('active');
                } else {
                    fragment.classList.remove('active');
                }
            });
        });
    });
});
