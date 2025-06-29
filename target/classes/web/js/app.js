document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('alunoForm');
    const tabela = document.getElementById('alunosTable').querySelector('tbody');

    // Carregar alunos ao iniciar
    carregarAlunos();

    // Cadastrar novo aluno
    form.addEventListener('submit', (e) => {
        e.preventDefault();
        
        const aluno = {
            nome: document.getElementById('nome').value,
            idade: document.getElementById('idade').value,
            email: document.getElementById('email').value,
            curso: document.getElementById('curso').value
        };

        fetch('/api/alunos', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(aluno)
        })
        .then(response => response.json())
        .then(() => {
            form.reset();
            carregarAlunos();
        });
    });

    function carregarAlunos() {
        fetch('/api/alunos')
            .then(response => response.json())
            .then(alunos => {
                tabela.innerHTML = '';
                alunos.forEach(aluno => {
                    const row = tabela.insertRow();
                    row.innerHTML = `
                        <td>${aluno.nome}</td>
                        <td>${aluno.idade}</td>
                        <td>${aluno.email}</td>
                        <td>${aluno.curso}</td>
                        <td>
                            <button onclick="removerAluno(${aluno.id})">Remover</button>
                        </td>
                    `;
                });
            });
    }

    window.removerAluno = (id) => {
        fetch(`/api/alunos/${id}`, { method: 'DELETE' })
            .then(() => carregarAlunos());
    };

    
    function editarAluno(id) {
    // Implementar lógica de edição
    }
});