# Gitlet Design Document

**Name**:

## Classes and Data Structures

### Class 1

#### Fields

1. Field 1
2. Field 2

### Class 2

#### Fields

1. Field 1
2. Field 2

## Algorithms

## Persistence

以下是为完成CS61B Project 2: Gitlet 设计的7-10步计划，采用迭代式开发的方法，逐步实现 Gitlet 版本控制系统的功能。每个步骤都包含明确的任务、指导和建议，旨在帮助你高效完成项目，同时确保代码结构清晰、可维护。计划中会结合项目规范（spec）的要求，并补充一些文本中未明确提到的实现细节和建议，以指导你逐步构建 Gitlet 系统。

---

### Gitlet 项目开发计划

#### 总体目标

实现一个简化的版本控制系统 Gitlet，支持初始化、添加文件、提交、查看日志、分支管理、合并等功能。项目需要使用 Java 实现，数据结构需要精心设计以满足运行时和内存要求，同时通过序列化机制实现数据持久化。以下计划将项目分解为8个步骤，每个步骤完成一部分功能，逐步构建完整的 Gitlet 系统。

---

### 步骤 1: 项目准备与初始化功能实现

**目标**: 搭建项目结构，完成 `init` 命令的实现，创建 Gitlet 的基本框架。

**任务**:

1. **创建项目目录结构**:

   - 在项目根目录下创建 `gitlet` 包，包含 `Main.java`（必须）、`Repository.java` 和 `Commit.java`（建议）。
   - 创建 `.gitlet` 目录结构，用于存储 Gitlet 的元数据和对象（例如提交和文件内容）。
   - 推荐的 `.gitlet` 目录结构：
     ```
     .gitlet/
       |-- commits/        # 存储提交对象的序列化文件
       |-- blobs/          # 存储文件内容的序列化文件
       |-- branches/       # 存储分支指针（指向提交的 SHA-1 ID）
       |-- HEAD            # 文件，存储当前分支名称
       |-- staging/        # 存储暂存区信息（添加和移除的文件）
     ```
2. **实现 `init` 命令**:

   - **功能**: 在当前目录创建 `.gitlet` 目录，初始化一个空的版本控制系统，生成初始提交（`initial commit`），并创建默认分支 `master`。
   - **实现细节**:
     - 检查当前目录是否已有 `.gitlet` 目录，若存在，抛出错误：`A Gitlet version-control system already exists in the current directory.`
     - 创建 `.gitlet` 目录及其子目录。
     - 创建初始提交对象（`Commit` 类），包含：
       - 提交消息：`initial commit`
       - 时间戳：1970年1月1日 00:00:00 UTC
       - 无文件（空的 blob 映射）
       - 无父提交（parent 为空）
     - 使用 SHA-1 哈希生成提交的唯一 ID（使用 `gitlet.Utils.sha1` 方法）。
     - 将初始提交序列化并存储到 `.gitlet/commits/` 目录下，文件名使用提交的 SHA-1 ID。
     - 创建 `master` 分支，存储在 `.gitlet/branches/master` 文件中，内容为初始提交的 SHA-1 ID。
     - 更新 `HEAD` 文件，记录当前分支为 `master`。
   - **序列化建议**:
     - 使用 Java 的 `ObjectOutputStream` 和 `ObjectInputStream` 序列化/反序列化 `Commit` 对象。
     - 确保 `Commit` 类实现 `java.io.Serializable` 接口。
     - 为避免序列化父提交对象，使用 SHA-1 ID 字符串而非直接的 `Commit` 对象引用（标记为 `transient`）。
3. **实现 `Main` 类基本框架**:

   - 在 `Main.java` 中实现 `main` 方法，解析命令行参数（`args`）。
   - 根据 `args[0]` 分发到不同的命令处理方法（例如 `init`）。
   - 处理全局错误情况：
     - 无参数：`Please enter a command.`
     - 无效命令：`No command with that name exists.`
     - 非 Gitlet 目录（除 `init` 外）：`Not in an initialized Gitlet directory.`
   - 建议在 `Main` 类中调用 `Repository` 类的方法处理具体逻辑，保持 `Main` 类简洁。
4. **测试**:

   - 编写简单的单元测试，验证 `init` 命令是否正确创建 `.gitlet` 目录、初始提交和 `master` 分支。
   - 测试错误情况：重复调用 `init` 是否抛出正确错误。
   - 使用 `gitlet.Utils` 提供的 `writeContents` 和 `readContentsAsString` 方法操作文件。

**指导**:

- **数据结构建议**:
  - `Commit` 类字段建议：
    ```java
    public class Commit implements Serializable {
        private String message; // 提交消息
        private Date timestamp; // 时间戳
        private Map<String, String> blobs; // 文件名到 blob SHA-1 ID 的映射
        private String parent; // 父提交的 SHA-1 ID
        // 可选：private transient Commit parentRef; // 运行时引用，序列化时忽略
    }
    ```
  - 使用 `HashMap<String, String>` 存储文件名到 blob SHA-1 ID 的映射。
- **SHA-1 使用**:
  - 使用 `gitlet.Utils.sha1` 方法为提交生成唯一 ID，输入包括消息、时间戳、blob 映射和父提交 ID。
  - 确保提交的 SHA-1 ID 是全局唯一的，内容相同的提交在不同机器上应生成相同 ID。
- **持久化**:
  - 将 `Commit` 对象序列化到 `.gitlet/commits/<SHA-1 ID>` 文件。
  - 使用 `gitlet.Utils.writeObject` 和 `readObject` 方法简化序列化操作。
- **错误处理**:
  - 使用 `System.exit(0)` 终止程序并输出错误消息。
- **时间戳**:
  - 使用 `java.util.Date` 创建时间戳，初始提交固定为 1970-01-01 00:00:00 UTC。

**预计时间**: 1-2 天
**完成标志**: `init` 命令正确运行，创建 `.gitlet` 目录，生成初始提交，`master` 分支指向初始提交，`HEAD` 指向 `master`。

---

### 步骤 2: 实现文件添加 (`add`) 和提交 (`commit`)

**目标**: 实现文件的暂存和提交功能，支持基本的版本控制操作。

**任务**:

1. **实现暂存区**:

   - 在 `.gitlet/staging/` 目录下创建两个文件：
     - `add`：存储待添加的文件（文件名到 blob SHA-1 ID 的映射）。
     - `remove`：存储待移除的文件（文件名列表）。
   - 设计暂存区数据结构，例如：
     ```java
     public class StagingArea implements Serializable {
         private Map<String, String> addedFiles; // 文件名 -> blob SHA-1 ID
         private List<String> removedFiles; // 待移除的文件名
     }
     ```
   - 初始化时，暂存区为空，序列化存储到 `.gitlet/staging/area`。
2. **实现 `add` 命令**:

   - **功能**: 将指定文件添加到暂存区，准备提交。
   - **实现细节**:
     - 检查文件是否存在，若不存在，抛出错误：`File does not exist.`
     - 读取工作目录中的文件内容，计算其 SHA-1 ID（使用 `gitlet.Utils.sha1` 和 `readContents`）。
     - 将文件内容序列化存储到 `.gitlet/blobs/<SHA-1 ID>`。
     - 将文件名和 blob SHA-1 ID 添加到暂存区的 `addedFiles` 映射。
     - 如果文件已暂存，覆盖之前的暂存内容。
     - 如果文件内容与当前提交（HEAD 指向的提交）中的版本相同，移除其暂存状态（从 `addedFiles` 和 `removedFiles` 中移除）。
     - 更新 `.gitlet/staging/area` 文件。
3. **实现 `commit` 命令**:

   - **功能**: 创建新提交，保存暂存区的文件快照，清空暂存区。
   - **实现细节**:
     - 检查暂存区是否为空，若为空，抛出错误：`No changes added to the commit.`
     - 检查提交消息是否为空，若为空，抛出错误：`Please enter a commit message.`
     - 获取当前 HEAD 提交（通过 `HEAD` 文件找到分支，再读取分支指向的提交）。
     - 创建新 `Commit` 对象：
       - 消息：从命令行参数获取。
       - 时间戳：当前时间（使用 `new Date()`）。
       - 父提交：当前 HEAD 提交的 SHA-1 ID。
       - 文件映射：复制父提交的 blob 映射，更新为暂存区的添加和移除内容。
     - 计算新提交的 SHA-1 ID，序列化存储到 `.gitlet/commits/<SHA-1 ID>`。
     - 更新当前分支（`.gitlet/branches/<branch>`）指向新提交的 SHA-1 ID。
     - 清空暂存区（重置 `addedFiles` 和 `removedFiles`）。
   - **注意**:
     - 提交不修改工作目录中的文件，仅更新 `.gitlet` 目录。
     - 使用 SHA-1 ID 确保文件内容重复时不存储多份（内容寻址）。
4. **测试**:

   - 测试 `add` 命令：添加文件，检查 `.gitlet/blobs` 和 `.gitlet/staging/area` 是否正确更新。
   - 测试 `commit` 命令：提交后检查新提交是否创建，分支指针是否更新，暂存区是否清空。
   - 测试错误情况：添加不存在文件、提交空暂存区、无提交消息。

**指导**:

- **暂存区管理**:
  - 使用 `HashMap` 和 `ArrayList` 管理暂存区的添加和移除文件。
  - 每次操作后序列化暂存区对象到 `.gitlet/staging/area`。
- **文件内容存储**:
  - 使用 `gitlet.Utils.readContents` 读取文件内容，`sha1` 计算 ID，`writeContents` 存储 blob。
  - 确保 blob 文件名是其内容的 SHA-1 ID，防止重复存储。
- **提交逻辑**:
  - 新提交的 blob 映射基于父提交，添加暂存区的 `addedFiles`，移除 `removedFiles` 中的文件。
  - 使用 `gitlet.Utils.writeObject` 序列化提交对象。
- **错误处理**:
  - 确保错误消息严格遵循规范，包括句尾的点号。
- **性能优化**:
  - `add` 命令的运行时应为 O(size of file + lg N)，其中 N 是提交中的文件数（比较文件内容时可能需要遍历）。
  - `commit` 命令的运行时应为 O(size of staged files)，避免复制未更改的文件内容。

**预计时间**: 2-3 天
**完成标志**: 可以添加文件到暂存区，提交更改生成新提交，分支指针和暂存区正确更新。

---

### 步骤 3: 实现文件移除 (`rm`) 和日志查看 (`log`)

**目标**: 支持移除文件并查看提交历史。

**任务**:

1. **实现 `rm` 命令**:

   - **功能**: 从暂存区移除文件（若已暂存添加），或标记为待移除（若当前提交跟踪该文件），并从工作目录中删除文件。
   - **实现细节**:
     - 检查文件是否在暂存区的 `addedFiles` 中，若存在，移除其暂存状态。
     - 检查文件是否在当前 HEAD 提交的 blob 映射中，若存在，将其添加到暂存区的 `removedFiles`。
     - 如果文件在工作目录中存在且在 HEAD 提交中跟踪，使用 `gitlet.Utils.restrictedDelete` 删除文件。
     - 如果文件既不在暂存区也不在 HEAD 提交中，抛出错误：`No reason to remove the file.`
     - 更新 `.gitlet/staging/area` 文件。
2. **实现 `log` 命令**:

   - **功能**: 显示从当前 HEAD 提交开始，沿第一父提交路径的提交历史。
   - **实现细节**:
     - 读取 `HEAD` 文件获取当前分支，读取分支文件获取当前提交的 SHA-1 ID。
     - 从当前提交开始，循环读取其父提交（使用 `parent` 字段的 SHA-1 ID），直到初始提交。
     - 对每个提交，输出以下格式：
       ```
       ===
       commit <SHA-1 ID>
       Date: <格式化时间戳>
       <提交消息>

       ```
     - 使用 `java.util.Date` 和 `java.util.Formatter` 格式化时间戳，显示为本地时区。
     - 对于合并提交（有第二父提交），在 `commit` 行后添加：
       ```
       Merge: <第一父提交前7位> <第二父提交前7位>
       ```
   - **注意**:
     - 提交按时间倒序显示（最新提交在顶部）。
     - 忽略合并提交的第二父提交，仅跟随第一父提交。
3. **测试**:

   - 测试 `rm` 命令：移除已暂存或跟踪的文件，检查暂存区和工作目录是否正确更新。
   - 测试 `log` 命令：创建多个提交，验证历史输出格式和内容正确。
   - 测试错误情况：移除不存在或未跟踪的文件。

**指导**:

- **移除逻辑**:
  - 确保 `rm` 只影响暂存区和工作目录中的文件，不直接修改提交。
  - 使用 `restrictedDelete` 防止意外删除非 Gitlet 文件。
- **日志输出**:
  - 使用 `gitlet.Utils.readObject` 读取提交对象，递归访问父提交。
  - 格式化时间戳时，参考 `SimpleDateFormat` 或 `Formatter` 的使用，确保符合规范示例。
- **性能优化**:
  - `rm` 命令运行时应为 O(1)，仅涉及少量文件操作。
  - `log` 命令运行时应为 O(N)，其中 N 是提交历史中的提交数。

**预计时间**: 1-2 天
**完成标志**: 可以移除文件并正确更新暂存区，`log` 命令显示正确的提交历史。

---

### 步骤 4: 实现文件检出 (`checkout`)

**目标**: 支持三种形式的 `checkout` 命令，恢复文件或切换分支。

**任务**:

1. **实现 `checkout -- [file name]`**:

   - **功能**: 从当前 HEAD 提交恢复指定文件到工作目录，不更改暂存区。
   - **实现细节**:
     - 获取当前 HEAD 提交，检查其 blob 映射是否包含指定文件。
     - 若文件不存在，抛出错误：`File does not exist in that commit.`
     - 使用 blob 的 SHA-1 ID 从 `.gitlet/blobs/` 读取文件内容，写入工作目录（使用 `gitlet.Utils.writeContents`）。
2. **实现 `checkout [commit id] -- [file name]`**:

   - **功能**: 从指定提交恢复指定文件到工作目录，不更改暂存区。
   - **实现细节**:
     - 根据提交 ID（支持缩短的 SHA-1 前缀）查找提交对象。
     - 若提交不存在，抛出错误：`No commit with that id exists.`
     - 检查提交的 blob 映射是否包含文件，若不存在，抛出错误：`File does not exist in that commit.`
     - 读取 blob 内容，写入工作目录。
     - **缩短 ID 处理**:
       - 遍历 `.gitlet/commits/` 目录，检查提交 ID 是否以输入的前缀开头。
       - 确保前缀唯一，若有多个匹配，抛出错误（可选，规范未要求）。
3. **实现 `checkout [branch name]`**:

   - **功能**: 切换到指定分支，恢复其 HEAD 提交中的所有文件，更新当前分支。
   - **实现细节**:
     - 检查分支是否存在，若不存在，抛出错误：`No such branch exists.`
     - 检查是否为当前分支，若是，抛出错误：`No need to checkout the current branch.`
     - 检查工作目录中是否有未跟踪文件会被覆盖，若有，抛出错误：`There is an untracked file in the way; delete it, or add and commit it first.`
     - 读取目标分支的 HEAD 提交，恢复其所有文件到工作目录。
     - 删除当前分支跟踪但目标分支不跟踪的文件。
     - 清空暂存区。
     - 更新 `HEAD` 文件，指向目标分支。
   - **未跟踪文件检查**:
     - 遍历工作目录文件，检查是否在当前提交的 blob 映射中。
     - 若文件未跟踪且会被目标分支的文件覆盖，抛出错误。
4. **测试**:

   - 测试三种 `checkout` 形式，验证文件恢复、分支切换和暂存区状态。
   - 测试错误情况：不存在的文件、提交或分支，未跟踪文件冲突。

**指导**:

- **文件恢复**:
  - 使用 `gitlet.Utils.writeContents` 写入文件内容，确保覆盖现有文件。
- **提交查找**:
  - 使用 `gitlet.Utils.plainFilenamesIn` 遍历 `.gitlet/commits/` 查找匹配的提交 ID。
  - 实现一个辅助方法，处理缩短的 SHA-1 ID 查找。
- **分支切换**:
  - 确保切换分支时正确处理文件删除和恢复。
  - 使用 `gitlet.Utils.restrictedDelete` 删除文件。
- **性能优化**:
  - 单文件 `checkout` 运行时为 O(size of file)。
  - 分支 `checkout` 运行时为 O(size of files in commit)，与提交数无关。

**预计时间**: 2-3 天
**完成标志**: 三种 `checkout` 命令正常工作，文件恢复和分支切换正确。

---

### 步骤 5: 实现分支管理 (`branch`, `rm-branch`) 和重置 (`reset`)

**目标**: 支持分支的创建、删除和重置到指定提交。

**任务**:

1. **实现 `branch` 命令**:

   - **功能**: 创建新分支，指向当前 HEAD 提交。
   - **实现细节**:
     - 检查分支名是否已存在，若存在，抛出错误：`A branch with that name already exists.`
     - 获取当前 HEAD 提交的 SHA-1 ID。
     - 创建 `.gitlet/branches/<branch name>` 文件，写入 HEAD 提交的 SHA-1 ID。
     - 不更改当前分支（`HEAD` 保持不变）。
2. **实现 `rm-branch` 命令**:

   - **功能**: 删除指定分支的指针，不影响提交。
   - **实现细节**:
     - 检查分支是否存在，若不存在，抛出错误：`A branch with that name does not exist.`
     - 检查是否为当前分支，若是，抛出错误：`Cannot remove the current branch.`
     - 删除 `.gitlet/branches/<branch name>` 文件。
3. **实现 `reset` 命令**:

   - **功能**: 将当前分支的 HEAD 移动到指定提交，恢复其文件，清空暂存区。
   - **实现细节**:
     - 检查提交 ID 是否存在，若不存在，抛出错误：`No commit with that id exists.`
     - 检查未跟踪文件是否会被覆盖，若有，抛出错误：`There is an untracked file in the way; delete it, or add and commit it first.`
     - 恢复指定提交的所有文件到工作目录。
     - 删除当前分支跟踪但指定提交不跟踪的文件。
     - 清空暂存区。
     - 更新当前分支（`.gitlet/branches/<current branch>`）指向指定提交的 SHA-1 ID。
   - **重用代码**:
     - 重用 `checkout [branch name]` 的文件恢复和删除逻辑。
4. **测试**:

   - 测试 `branch` 和 `rm-branch`，验证分支创建和删除，HEAD 不变。
   - 测试 `reset`，验证文件恢复、分支指针更新和暂存区清空。
   - 测试错误情况：重复分支名、删除当前分支、无效提交 ID。

**指导**:

- **分支管理**:
  - 分支仅是 `.gitlet/branches/` 下的文件，内容为提交的 SHA-1 ID。
  - 使用 `gitlet.Utils.writeContents` 和 `restrictedDelete` 管理分支文件。
- **重置逻辑**:
  - `reset` 等效于 `checkout [commit id]` 加上分支指针更新。
  - 确保未跟踪文件检查与 `checkout [branch name]` 一致。
- **性能优化**:
  - `branch` 和 `rm-branch` 运行时为 O(1)。
  - `reset` 运行时为 O(size of files in commit)，与提交数无关。

**预计时间**: 1-2 天
**完成标志**: 分支创建、删除和重置功能正常工作。

---

### 步骤 6: 实现全局日志 (`global-log`) 和查找 (`find`)

**目标**: 支持查看所有提交和根据消息查找提交。

**任务**:

1. **实现 `global-log` 命令**:

   - **功能**: 显示所有提交的信息（不限当前分支），顺序无关。
   - **实现细节**:
     - 遍历 `.gitlet/commits/` 目录，读取每个提交对象。
     - 输出格式与 `log` 命令相同，包含 SHA-1 ID、时间戳、消息和可能的合并信息。
     - 使用 `gitlet.Utils.plainFilenamesIn` 获取所有提交 ID。
2. **实现 `find` 命令**:

   - **功能**: 查找具有指定提交消息的提交 ID，按行输出。
   - **实现细节**:
     - 遍历 `.gitlet/commits/` 目录，检查每个提交的 `message` 是否匹配输入。
     - 若匹配，输出提交的 SHA-1 ID。
     - 若无匹配，抛出错误：`Found no commit with that message.`
     - 支持多词消息（命令行参数需用引号括起来）。
3. **测试**:

   - 测试 `global-log`，验证所有提交（包括不同分支）正确显示。
   - 测试 `find`，验证匹配消息的提交 ID 输出，测试多词消息和无匹配情况。

**指导**:

- **遍历提交**:
  - 使用 `gitlet.Utils.plainFilenamesIn` 高效遍历 `.gitlet/commits/`。
  - 重用 `log` 命令的输出格式化逻辑。
- **消息匹配**:
  - 使用 `String.equals` 比较提交消息，确保精确匹配。
- **性能优化**:
  - 两者运行时为 O(N)，其中 N 是所有提交数。
  - 避免重复读取提交对象，缓存已读取的对象（可选）。

**预计时间**: 1 天
**完成标志**: `global-log` 显示所有提交，`find` 正确查找提交。

---

### 步骤 7: 实现状态查看 (`status`) 和合并 (`merge`)

**目标**: 支持查看 Gitlet 状态和分支合并（包括冲突处理）。

**任务**:

1. **实现 `status` 命令**:

   - **功能**: 显示当前分支、所有分支、暂存区文件和（可选）修改未暂存及未跟踪文件。
   - **实现细节**:
     - 输出格式：
       ```plaintext
       === Branches ===
       *<当前分支>
       <其他分支，按字典序>

       === Staged Files ===
       <暂存添加的文件，按字典序>

       === Removed Files ===
       <暂存移除的文件，按字典序>

       === Modifications Not Staged For Commit ===
       <修改未暂存的文件，按字典序>（额外信用）

       === Untracked Files ===
       <未跟踪文件，按字典序>（额外信用）

       ```
     - 分支：读取 `.gitlet/branches/` 目录，当前分支加 `*` 前缀。
     - 暂存文件：读取暂存区的 `addedFiles` 和 `removedFiles`。
     - 修改未暂存（额外信用）：
       - 跟踪但工作目录中修改或删除的文件。
       - 暂存添加但工作目录中内容不同或删除的文件。
     - 未跟踪文件（额外信用）：
       - 工作目录中存在但未跟踪（不在当前提交或暂存区）的文件。
     - 使用 `gitlet.Utils.plainFilenamesIn` 遍历工作目录。
2. **实现 `merge` 命令**:

   - **功能**: 合并指定分支到当前分支，处理文件更改和冲突。
   - **实现细节**:
     - 检查暂存区是否为空，若不为空，抛出错误：`You have uncommitted changes.`
     - 检查分支是否存在或是否为当前分支，若无效，抛出相应错误。
     - 查找当前分支和目标分支的最近公共祖先（split point）：
       - 使用 BFS 或 DFS 遍历两个分支的祖先，找到最晚的公共提交。
     - 如果 split point 是目标分支，输出 `Given branch is an ancestor of the current branch.` 并退出。
     - 如果 split point 是当前分支，执行 `checkout [branch name]`，输出 `Current branch fast-forwarded.` 并退出。
     - 合并逻辑：
       - 目标分支修改、当前分支未修改的文件：检出并暂存。
       - 当前分支修改、目标分支未修改的文件：保持不变。
       - 两分支相同修改：保持不变。
       - 仅当前分支有新文件：保持不变。
       - 仅目标分支有新文件：检出并暂存。
       - split point 存在、当前分支未修改、目标分支移除：移除并取消跟踪。
       - split point 存在、目标分支未修改、当前分支移除：保持移除。
       - 两分支不同修改（包括删除）：生成冲突文件，格式为：
         ```
         <<<<<<< HEAD
         当前分支文件内容
         =======
         目标分支文件内容
         >>>>>>>
         ```
       - 冲突文件暂存。
     - 创建合并提交：
       - 消息：`Merged [given branch name] into [current branch name].`
       - 两个父提交：当前分支 HEAD 和目标分支 HEAD。
       - 若有冲突，输出：`Encountered a merge conflict.`
   - **注意**:
     - 检查未跟踪文件是否会被覆盖，与 `checkout [branch name]` 一致。
     - 更新当前分支指针指向新合并提交。
3. **测试**:

   - 测试 `status`，验证分支、暂存区和额外信用部分的输出。
   - 测试 `merge`，验证文件合并、冲突处理、快进合并和祖先情况。

**指导**:

- **状态输出**:
  - 使用 `TreeSet` 确保文件和分支按字典序输出。
  - 比较文件内容时，使用 SHA-1 ID 判断是否修改。
- **合并算法**:
  - 实现一个辅助方法查找 split point，建议使用 BFS 遍历祖先。
  - 重用 `checkout` 和 `add` 逻辑处理文件恢复和暂存。
  - 冲突文件内容直接拼接，使用 `StringBuilder` 构造。
- **性能优化**:
  - `status` 运行时依赖工作目录文件数、暂存文件数和分支数。
  - `merge` 运行时为 O(N log N + D)，其中 N 是祖先提交数，D 是文件总大小。

**预计时间**: 3-4 天
**完成标志**: `status` 显示正确状态，`merge` 正确处理合并和冲突。

---

### 步骤 8: 测试、优化和文档

**目标**: 完善测试用例，优化代码，提交设计文档。

**任务**:

1. **编写全面测试**:

   - 为每个命令编写单元测试，覆盖正常和错误情况。
   - 模拟复杂场景，例如多分支、冲突合并、文件覆盖等。
   - 使用 `gitlet.DumpObj` 调试序列化文件内容。
2. **代码优化**:

   - 检查性能瓶颈，确保满足运行时要求。
   - 优化文件 I/O 操作，减少不必要的序列化/反序列化。
   - 重构重复代码，提取公共方法。
3. **编写设计文档**:

   - 描述项目结构、类设计、数据结构选择和持久化策略。
   - 说明每个命令的实现逻辑和关键算法（如 split point 查找）。
   - 示例设计文档结构：
     - **概述**: 项目目标和功能。
     - **类结构**: 每个类的职责和字段。
     - **数据结构**: 提交、暂存区、分支的表示。
     - **持久化**: `.gitlet` 目录结构和序列化机制。
     - **命令实现**: 每个命令的流程和关键点。
4. **提交到 Gradescope**:

   - 提交到 Checkpoint Grader（3/12）和 Full Grader（4/2）。
   - 推送 snaps 仓库到 Snaps Grader（4/9）。

**指导**:

- **测试策略**:
  - 使用 JUnit 编写测试，模拟命令行调用（通过 `Main.main`）。
  - 检查 `.gitlet` 目录内容、文件系统状态和命令输出。
- **优化建议**:
  - 使用 `HashMap` 缓存提交对象，减少反序列化开销。
  - 确保 SHA-1 ID 使用一致，避免重复计算。
- **设计文档**:
  - 保持简洁但全面，突出设计决策和权衡。
  - 示例参考 Lab 6 的设计文档。

**预计时间**: 2-3 天
**完成标志**: 通过所有测试，提交设计文档和代码。

---

### 额外建议

- **迭代开发**: 每个步骤完成后运行测试，确保功能正确，避免后期 debug 复杂。
- **版本控制**: 使用 Git（非 Gitlet）管理项目代码，定期提交。
- **调试工具**: 使用 `gitlet.DumpObj` 检查序列化文件，验证提交和 blob 内容。
- **协作与资源**:
  - 查看提供的视频资源（Git 介绍、合并概览等）。
  - 在 Ed 或 Gitbug 上与同学讨论设计思路，但避免分享具体代码。
- **时间管理**: 按步骤规划时间，留出余量调试 `merge` 等复杂命令。

---

### 时间表总结

- **步骤 1**: 1-2 天（初始化）
- **步骤 2**: 2-3 天（添加和提交）
- **步骤 3**: 1-2 天（移除和日志）
- **步骤 4**: 2-3 天（检出）
- **步骤 5**: 1-2 天（分支和重置）
- **步骤 6**: 1 天（全局日志和查找）
- **步骤 7**: 3-4 天（状态和合并）
- **步骤 8**: 2-3 天（测试和文档）
- **总计**: 约 13-20 天

通过分步实现和测试，你可以逐步构建一个功能完整的 Gitlet 系统，同时保持代码清晰和可维护。祝你 coding 愉快！
