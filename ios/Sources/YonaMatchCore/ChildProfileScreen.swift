import SwiftUI

@available(iOS 16.0, *)
struct ChildProfileScreen: View {
    let onContinue: (String, String, String) -> Void
    @State private var name = "Yona"
    @State private var avatar = 0
    @State private var age = "3-4"
    private let avatars = ["🦁", "🐰", "🐼", "🐘"]

    var body: some View {
        ScrollView {
            VStack(spacing: 24) {
                Spacer(minLength: 28)
                Text("Kendi Profilini Oluştur")
                    .font(.system(size: 24, weight: .bold))
                HStack {
                    Button { avatar = (avatar + 3) % 4 } label: {
                        Image(systemName: "chevron.left")
                            .font(.system(size: 26, weight: .bold))
                            .frame(width: 48, height: 48)
                            .background(.blue.opacity(0.2), in: Circle())
                    }
                    Spacer()
                    Text(avatars[avatar])
                        .font(.system(size: 100))
                        .frame(width: 160, height: 160)
                        .background(Color.yellow.opacity(0.6), in: Circle())
                    Spacer()
                    Button { avatar = (avatar + 1) % 4 } label: {
                        Image(systemName: "chevron.right")
                            .font(.system(size: 26, weight: .bold))
                            .frame(width: 48, height: 48)
                            .background(.blue.opacity(0.2), in: Circle())
                    }
                }
                HStack(spacing: 16) {
                    ForEach(avatars.indices.filter { $0 != avatar }.prefix(3), id: \.self) { index in
                        Button { avatar = index } label: {
                            Text(avatars[index]).font(.system(size: 52))
                                .frame(width: 76, height: 76)
                                .background(.cyan.opacity(0.2), in: Circle())
                        }
                    }
                }
                TextField("Adın", text: $name)
                    .font(.system(size: 21))
                    .multilineTextAlignment(.center)
                    .padding(14)
                    .background(.white, in: Capsule())
                    .overlay(Capsule().stroke(.gray.opacity(0.3)))
                Text("Yaş Grubu").font(.system(size: 18, weight: .semibold))
                HStack(spacing: 10) {
                    ForEach(["3-4", "5-6", "7-8"], id: \.self) { option in
                        Button { age = option } label: {
                            Text(option).font(.system(size: 20, weight: .bold))
                                .frame(maxWidth: .infinity, minHeight: 56)
                                .background(age == option ? Color.green : Color.gray.opacity(0.1),
                                            in: Capsule())
                                .foregroundStyle(age == option ? .white : .primary)
                        }
                    }
                }
                Button { onContinue(name.trimmingCharacters(in: .whitespacesAndNewlines), avatars[avatar], age) } label: {
                    Text("Devam").font(.system(size: 23, weight: .bold))
                        .frame(maxWidth: .infinity, minHeight: 66)
                        .background(Color.yellow, in: RoundedRectangle(cornerRadius: 28))
                }
                .disabled(name.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty)
                .foregroundStyle(.black)
            }
            .padding(24)
        }
        .background(Color(red: 1, green: 0.976, blue: 0.941).ignoresSafeArea())
    }
}
