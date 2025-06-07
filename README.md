# Forge 1.20.1 – TPose Animation With Keybind

This mod demonstrates how to track a keybind press on the client, send that state to the server, synchronize it across all clients, and render a custom T‑Pose animation based on a per‑player capability.  
It is fully multiplayer‑compatible and uses **Minecraft Forge 1.20.1** networking and capability systems.
![image1](https://github.com/user-attachments/assets/b874d25d-7690-4df9-b571-4a333508ff6f)


---

## ✅ Features
- Custom keybind (`P`), or whatever keybind you choose, toggles the T‑Pose animation for **your** player.  
- The T‑Pose state is stored per‑player using a Forge capability.  
- The server receives key‑press packets and updates the server‑side capability.  
- The server then broadcasts the updated state to **all** clients.  
- Clients render the T‑Pose only for players whose capability is **true**.

---

## 🔁 How It Works

### 1. Keybind Detection (client)
`ClientKeyEventHandler` checks the key every tick:

```java
public static boolean isDown;
isDown = Keybinds.PEE_KEY.isDown();// is the desired Hot key being pressed

if (isDown) {   //if the hotkey is being pressed
    TPoseCapability.setPlayerTPose(player, true);   //set the t-pose capability for that player to true
    MyModNetwork.CHANNEL.sendToServer(new TPoseTogglePacket(true));     //send a Tpose toggle packet to the server to let it know someone is tposing
} else { // the hotkey is not being pressed
    TPoseCapability.setPlayerTPose(player, false);// set the t-pose capability for that player to false
    MyModNetwork.CHANNEL.sendToServer(new TPoseTogglePacket(false));// send a Tpose toggle packet to the server to let it know someone stopped tposing

}
```

### 2. Server Handling & Broadcast
The server receives `TPoseTogglePacket`:

```java
// Update their TPose state on the server
sender.getCapability(TPoseCapability.CAP).ifPresent(cap -> cap.setTPose(msg.isTPose));

// Broadcast the new TPose state to all clients
MyModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(),
        new TPoseSyncPacket(sender.getUUID(), msg.isTPose));
```

### 3. Client Sync Handler
Every client receives `TPoseSyncPacket` and updates the capability for **that** player:

```java
Player p = world.getPlayerByUUID(msg.playerId);
p.getCapability(TPoseCapability.CAP).ifPresent(cap -> cap.setTPose(msg.isTPose));

// or use the TPoseCapability Class (easier)

TPoseCapability.setPlayerTPose(player, msg.isTPose);
```

### 4. Capability Storage
`TPoseCapability` attaches to each player and stores a single boolean:

```java
private boolean isTPose;
public boolean isTPose() { return isTPose; }
public void setTPose(boolean v) { isTPose = v; }

//This method returns whether the player is T-posing or not.
public static boolean isPlayerTPose(Player player) {
    return player.getCapability(TPoseCapability.CAP)
            .map(TPoseCapability::isTPose)
            .orElse(false);
}

//This method sets the T-posing status of a specific player using capability.
public static void setPlayerTPose(Player player, boolean value) {//
    player.getCapability(TPoseCapability.CAP).ifPresent(cap -> cap.setTPose(value));
}

```

### 5. Rendering the T‑Pose
We go about rendering the TPose by first hiding the vanilla arms, then rendering new rotated arms on top. Once we want to stop the animation, we hide the new rotated arms, then show the original vanilla arms again:

`HideVanillaArms.java` 
```java
public static void onRenderPlayerPre(RenderPlayerEvent.Pre evt) {//event is fired before the player entity is rendered on the client-side
    PlayerModel<AbstractClientPlayer> m =
            ((PlayerRenderer) evt.getRenderer()).getModel();

    Player p = evt.getEntity();

    if (TPoseCapability.isPlayerTPose(p) ){// check the TPose capability of the player to see if we should hide the vanilla arms
        //hide the vanilla arms of the player. (we want to hide the vanilla arms so we can render separate ones on another layer)
        m.rightArm.visible = false;
        m.leftArm.visible  = false;
        m.rightSleeve.visible = false;
        m.leftSleeve.visible  = false;
    }
}
```

`TPosedArms.java` 
```java
public void render(PoseStack poseStack, MultiBufferSource buffer, int light,
                    AbstractClientPlayer player, float limbSwing, float limbSwingAmount,
                    float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {

    if (TPoseCapability.isPlayerTPose(player)){// check the TPose capability of the player to see if we should render the new arms
        PlayerModel<AbstractClientPlayer> model = getParentModel();

        // Copy current state before modifying
        poseStack.pushPose();

        // Ensure we use the correct pose (like sneaking offset, head/body rotation etc.)
        model.setupAnim(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        // Ensure arms are visible (temporarily)
        model.leftArm.visible = true;
        model.rightArm.visible = true;

        // Apply the T-pose manually
        model.leftArm.zRot = (float) -Math.PI / 2F;
        model.rightArm.zRot = (float) Math.PI / 2F;
        model.leftArm.xRot = model.rightArm.xRot = 0F;
        model.leftArm.yRot = model.rightArm.yRot = 0F;

        // Get player skin
        ResourceLocation skin = player.getSkinTextureLocation();
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(skin));

        // Only render the arms (not the full model)
        model.leftArm.render(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
        model.rightArm.render(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
    }
}

```

The layer is added to every `PlayerRenderer` in `EntityRenderersEvent.AddLayers`.

---

## 🧠 Data Flow Summary

| Stage            | Class / Packet            | Responsibility |
|------------------|---------------------------|----------------|
| **Input**        | `ClientKeyEventHandler`   | Detect key; update local capability; send `TPoseTogglePacket` |
| **Server Update**| `TPoseTogglePacket`       | Update server‑side capability; broadcast `TPoseSyncPacket` |
| **Client Sync**  | `TPoseSyncPacket`         | Update capability for the specified player |
| **Storage**      | `TPoseCapability`         | Persist per‑player T‑Pose flag |
| **Rendering**    | `HideVanillaArms & TPosedArms`              | Render arms in T‑Pose when flag is true / hide vanilla arms when Tpose flag is false |

---

## 🛠 Setup Notes
1. Call `MyModNetwork.register()` during mod initialization.  
2. Attach `TPoseCapability` to players in `AttachCapabilitiesEvent<Entity>`.  
3. Register `HideVanillaArms` in `EntityRenderersEvent.AddLayers` **client‑side only**.  
4. Register the keybind in `RegisterKeyMappingsEvent`.

---

## 🔑 Default Keybind

| Action | Default Key |
|--------|-------------|
| Toggle T‑Pose | **P** |

---

Enjoy striking a perfect T‑Pose in multiplayer!
