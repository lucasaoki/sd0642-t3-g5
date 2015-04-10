Sistema de Arquivo com P2P

---

O objetivo é criar um sistema P2P (Peer to Peer) centralizado para a descoberta e manipulação de arquivos.

O nó central (mestre do cluster) deve ter as listas dos nós disponíveis e os arquivos que os respectivos nós clientes (restante dos nós do cluster) possuem.
Uma vez realizada a descoberta do arquivo, o nó solicitante se conecta diretamente ao nó que possui o recurso.

Não deve ser permitido que um nó faça operações (escrita, exclusão) em um arquivo que esteja sendo manipulado por outro nó.
As operações que devem ser realizadas entre os nós são:
Criação, leitura, exclusão e movimentação de arquivos entre os nós (atualização de localidade)

Tratar problemas de ter arquivos duplicados em todos os nós clientes, ou seja, um mesmo arquivo pode estar em 1 ou N nós e quando este arquivo é manipulado, os demais nós que contém este arquivo devem ser capazes de fazer essa atualização.
É obrigatório garantir os seguintes princípios em relação aos sistemas distribuídos:
  * Transparência: Por exemplo, quando um arquivo é movimentado de um nó x para o nó y, todos os nós restantes precisam ser informados
  * Exclusão Mútua
  * Deadlock
  * Sincronismo

Todo o desenvolvimento do trabalho deve ser implementado utilizando o cluster

O desenvolvimento completo do trabalho deve ser feito em JAVA e JXTA.

Modelagem do problema utilizando UML

Considerar diagramas de:
  * Classe
  * Colaboração
  * Estado
  * Casos de Uso

Além dos códigos das aplicações cliente e servidores é preciso gerar casos que tenham problemas de:
  * Concorrência, Deadlock, Sincronismo e Exclusão Mútua