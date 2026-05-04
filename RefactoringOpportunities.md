## Issue #20 - Game

1. **Extract Method** em `printBoard()` para separar:
    - criação do tabuleiro
    - colocação dos navios
    - aplicação dos tiros
    - impressão do cabeçalho/linhas/legenda

2. **Extract Method** em `randomEnemyFire()` para separar:
    - construção das posições utilizáveis
    - geração aleatória dos tiros
    - impressão da rajada

3. **Extract Method** em `fireShots()` com `validateShotCount()` para isolar a validação do número de tiros.

4. **Rename Variable** em `printBoard()`:
    - `ship_pos` -> `shipPos`
    - `adjacent_pos` -> `adjacentPos`

5. **Replace Duplication / Factory Method** com `createIndentedObjectMapper()` para evitar repetir a configuração do `ObjectMapper`.