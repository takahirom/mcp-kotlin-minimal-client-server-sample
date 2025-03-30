# MCP(Model Context Protocol) minimal Kotlin client server sample

A simple weather tool demonstrating server-client interaction using the Model Context Protocol (MCP). For demonstration purposes only.

User: "What's the weather in Tokyo?"  
Response: "The weather in Tokyo is sunny."

This is a Kotlin version of [mcp-minimal-client-weather-server-sample](https://github.com/takahirom/mcp-minimal-client-weather-server-sample)

client: src/main/kotlin/Client.kt
server: server.main.kts

**Diagram 1: Initialization and Tool Discovery**

```mermaid
sequenceDiagram
    autonumber

    participant User
    participant ClientApp as ClientApp (Host)<br>on Local PC
    participant LLM as LLM (e.g., Claude)<br>Remote Service
    participant MCPClient as MCPClient (Internal Component)<br>in ClientApp on Local PC
    participant MCPServer as MCPServer (e.g., Tool Server)<br>on Local PC

    Note over User, MCPServer: User starts ClientApp, initiating connection to MCPServer

    ClientApp->>+MCPClient: Instruct preparation to connect to a specific MCPServer
    Note right of ClientApp: Host manages MCPClient instances per server

    MCPClient->>+MCPServer: 1. initialize (Request)<br>[protocol_version, client_capabilities]
    Note over MCPClient, MCPServer: Start connection establishment and capability exchange (JSON-RPC)
    MCPServer-->>-MCPClient: 2. initialize (Response)<br>[selected_protocol_version, server_capabilities (tools, resources, etc.)]
    Note over MCPClient, MCPServer: Server notifies its available capabilities

    MCPClient->>MCPServer: 3. notifications/initialized (Initialization Complete Notification)
    Note over MCPClient, MCPServer: Handshake complete, normal communication possible

    MCPClient-->>-ClientApp: Initialization Success & Server Capabilities notified (implicitly via successful initialize await)
    Note right of ClientApp: Host (ClientApp) now knows the server is ready

    ClientApp->>MCPClient: 4. Instruct to get the list of tools provided by the server (session.list_tools())
    MCPClient->>+MCPServer: 5. tools/list (Request)
    Note over MCPClient, MCPServer: Request the list of tools defined on the server
    MCPServer-->>-MCPClient: 6. tools/list (Response)<br>[{"name": "get_weather", "description": "...", "inputSchema": {...}}]
    Note over MCPClient, MCPServer: Example: Returns the definition of the 'get_weather' tool

    MCPClient-->>ClientApp: 7. Notify Tool List (containing 'get_weather', etc.) via tools_response object
    Note right of ClientApp: Host stores/processes the retrieved tool information (format_tools_for_llm)

    ClientApp->>LLM: 8. Send available tool information to LLM<br>(e.g., via system prompt using formatted tools_prompt)
    Note over ClientApp, LLM: Host informs LLM that 'get_weather' is available<br>LLM can now decide to use the tool based on this info
```

**Diagram 2: Tool Execution Flow**

```mermaid
sequenceDiagram
    autonumber

    participant User
    participant ClientApp as ClientApp (Host)<br>on Local PC
    participant LLM as LLM (e.g., Claude)<br>Remote Service
    participant MCPClient as MCPClient (Internal Component)<br>in ClientApp on Local PC
    participant MCPServer as MCPServer (e.g., Tool Server)<br>on Local PC

    User->>ClientApp: 1. "What's the weather in Tokyo?"
    Note right of User: User asks a question that might require a tool

    ClientApp->>LLM: 2. Forward user's question with context<br>[System Prompt (with tool info) + User Question]
    Note over ClientApp, LLM: Host sends the question and formatted tool info ('get_weather') to the LLM

    LLM->>ClientApp: 3. LLM responds, requesting tool usage<br>Response: `{"tool_name": "get_weather", "arguments": {"location": "Tokyo"}}`
    Note over ClientApp, LLM: Based on provided tool info, LLM decides to use the weather tool<br>and generates the required JSON structure with arguments

    ClientApp->>User: (Optional) 4. Confirm tool execution<br>"Execute 'get_weather' on Tool Server?"
    User->>ClientApp: (Optional) 5. "Yes"
    Note over ClientApp, User: For security/transparency, host might seek user permission (Not implemented in the sample code)

    ClientApp->>+MCPClient: 6. Instruct execution of 'get_weather' tool (session.call_tool())<br>Arguments: {"location": "Tokyo"}
    Note right of ClientApp: Host instructs the MCP server via MCPClient

    MCPClient->>+MCPServer: 7. tools/call (Request)<br>[name: "get_weather", arguments: {"location": "Tokyo"}]
    Note over MCPClient, MCPServer: Invoke server function with specified tool name and arguments (JSON-RPC)

    MCPServer->>MCPServer: Internal processing (executes get_weather function)
    Note over MCPServer: Logs execution, returns result (e.g., "Sunny")
    MCPServer-->>-MCPClient: 8. tools/call (Response)<br>[result: {content: [{type: "text", text: "Sunny"}]}]
    Note over MCPClient, MCPServer: Server returns the processing result (weather info) in structured format

    MCPClient-->>-ClientApp: 9. Notify Tool Execution Result<br>Result object contains: "Sunny" (extracted from tool_result_obj.content[0].text)

    ClientApp->>LLM: 10. Send tool execution result back to LLM<br>New User Message: "Tool 'get_weather' returned: 'Sunny'. Answer the original question based on this."
    Note over ClientApp, LLM: Host feeds back the information obtained from the server to the LLM

    LLM->>ClientApp: 11. Generate final response (as text)<br>"The weather in Tokyo is Sunny."

    ClientApp->>User: 12. Display the final response generated by LLM
    Note right of ClientApp: Present the final answer to the user
```